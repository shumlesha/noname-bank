import {formatCurrency, formatDate} from '../utils/format-utils.js';
import websocketService from '../services/websocket-service.js';
import API_CONFIG from '../config/api-config.js';

let currentAccountId = null;
const TRANSACTIONS_WS_CALLBACK_ID = 'transactions-list-updates';


export const initTransactionsWebSocket = async (accountId, container) => {
    if (!accountId) {
        console.error('Cannot initialize WebSocket: No account ID provided');
        return;
    }

    if (currentAccountId && currentAccountId !== accountId) {
        cleanupTransactionsWebSocket();
    }

    currentAccountId = accountId;

    if (container) {
        container.innerHTML = '<h3>Транзакции по счету</h3><div class="loading-indicator"><span class="loading-text">Загрузка транзакций...</span></div>';
    }

    try {
        if (!websocketService.isConnected()) {
            const wsUrl = `${API_CONFIG.WS_BASE_URL}${API_CONFIG.ENDPOINTS.wsTransactionEmployee}`;
            await websocketService.connect(wsUrl);
        }

        const transactions = [];

        websocketService.subscribe(TRANSACTIONS_WS_CALLBACK_ID, (transaction) => {
            if (transaction &&
                (transaction.accountFrom === accountId || transaction.accountTo === accountId)) {

                const existingIndex = transactions.findIndex(tx => tx.id === transaction.id);
                const isNewTransaction = existingIndex === -1;

                if (isNewTransaction) {
                    transactions.push(transaction);

                    if (container) {
                        updateTransactionsList(transaction, container);
                    }
                }
            }
        });

        websocketService.send(accountId);
        console.log('Subscribed to transaction updates for account:', accountId);
    } catch (error) {
        console.error('Failed to initialize WebSocket connection:', error);
        if (container) {
            container.innerHTML = '<h3>Транзакции по счету</h3><div class="error-message-container"><p class="error-message">Ошибка при подключении к серверу транзакций</p></div>';
        }
    }
};


export const cleanupTransactionsWebSocket = () => {
    websocketService.unsubscribe(TRANSACTIONS_WS_CALLBACK_ID);
    currentAccountId = null;

    if (Object.keys(websocketService.callbacks).length === 0) {
        websocketService.disconnect();
    }
};


export const updateTransactionsList = (newTransaction, container) => {
    if (!newTransaction || !container) return;

    const existingTable = container.querySelector('.transactions-table');
    if (!existingTable) {
        renderTransactionsList([newTransaction], container);
        return;
    }

    const tbody = existingTable.querySelector('tbody');
    if (!tbody) {
        console.error('Не найден tbody в таблице транзакций');
        return;
    }


    const rows = tbody.querySelectorAll('tr');
    let transactionExists = false;
    
    for (const row of rows) {
        const idCell = row.querySelector('td:first-child');
        if (idCell && idCell.textContent === newTransaction.id) {
            transactionExists = true;
            break;
        }
    }
    
    if (transactionExists) {
        return;
    }

    try {
        const fragment = document.createDocumentFragment();
        const newRow = document.createElement('tr');

        const transactionDate = formatDate(newTransaction.transactionTimestamp);
        const accountFrom = newTransaction.accountFrom || 'Пополнение';
        const accountTo = newTransaction.accountTo || 'Нет данных';
        const currency = newTransaction.currency || 'RUB';
        const amount = formatCurrency(newTransaction.amount, currency);

        newRow.innerHTML = `
            <td>${newTransaction.id || 'Нет данных'}</td>
            <td>${transactionDate}</td>
            <td>${accountFrom}</td>
            <td>${accountTo}</td>
            <td>${amount}</td>
        `;

        fragment.appendChild(newRow);

        if (tbody.firstChild) {
            tbody.insertBefore(fragment, tbody.firstChild);
        } else {
            tbody.appendChild(fragment);
        }
    } catch (error) {
        console.error('Error adding new transaction to table:', error);
    }
};

export const renderTransactionsList = (transactions, container) => {
    console.log('Rendering transactions list:', transactions);

    if (!transactions || !transactions.length) {
        container.innerHTML = '<h3>Транзакции по счету</h3><div class="error-message-container"><p class="info-message">Транзакции не найдены</p></div>';
        return;
    }

    const table = document.createElement('table');
    table.className = 'transactions-table';

    table.innerHTML = `
    <thead>
      <tr>
        <th>ID транзакции</th>
        <th>Дата и время</th>
        <th>Счет отправителя</th>
        <th>Счет получателя</th>
        <th>Сумма</th>
      </tr>
    </thead>
    <tbody>
      ${transactions.map(tx => {
        try {
            const transactionDate = formatDate(tx.transactionTimestamp);
            const accountFrom = tx.accountFrom || 'Пополнение';
            const accountTo = tx.accountTo || 'Нет данных';
            const currency = tx.currency || 'RUB';
            const amount = formatCurrency(tx.amount, currency);

            return `<tr>
              <td>${tx.id || 'Нет данных'}</td>
              <td>${transactionDate}</td>
              <td>${accountFrom}</td>
              <td>${accountTo}</td>
              <td>${amount}</td>
            </tr>`;
        } catch (error) {
            console.error('Error rendering transaction:', tx, error);
            return `<tr><td colspan="6">Ошибка отображения транзакции</td></tr>`;
        }
    }).join('')}
    </tbody>
  `;

    container.innerHTML = '<h3>Транзакции по счету</h3>';
    container.appendChild(table);
};