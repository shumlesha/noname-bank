import {formatCurrency, formatDate} from '../utils/format-utils.js';
import websocketService from '../services/websocket-service.js';
import API_CONFIG from '../config/api-config.js';

let currentAccountId = null;
const TRANSACTIONS_WS_CALLBACK_ID = 'transactions-list-updates';
let transactionsCache = [];

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
        container.innerHTML = '<div class="loading-indicator"><span class="loading-text">Загрузка транзакций...</span></div>';
    }

    try {
        if (!websocketService.isConnected()) {
            const wsUrl = `${API_CONFIG.WS_BASE_URL}${API_CONFIG.ENDPOINTS.wsTransactionEmployee}`;
            await websocketService.connect(wsUrl);
        }

        // Reset transactions cache for the new account
        transactionsCache = [];
        
        // Create empty transactions table
        if (container) {
            createEmptyTransactionsTable(container);
        }

        websocketService.subscribe(TRANSACTIONS_WS_CALLBACK_ID, (transaction) => {
            if (transaction &&
                (transaction.accountFrom === accountId || transaction.accountTo === accountId)) {

                const existingIndex = transactionsCache.findIndex(tx => tx.id === transaction.id);
                const isNewTransaction = existingIndex === -1;

                if (isNewTransaction) {
                    transactionsCache.push(transaction);

                    if (container) {
                        addTransactionToTable(transaction, container);
                    }
                }
            }
        });

        websocketService.send(accountId);
        console.log('Subscribed to transaction updates for account:', accountId);
    } catch (error) {
        console.error('Failed to initialize WebSocket connection:', error);
        if (container) {
            container.innerHTML = '<div class="error-message-container"><p class="error-message">Ошибка при подключении к серверу транзакций</p></div>';
        }
    }
};

export const cleanupTransactionsWebSocket = () => {
    websocketService.unsubscribe(TRANSACTIONS_WS_CALLBACK_ID);
    currentAccountId = null;
    transactionsCache = [];

    if (Object.keys(websocketService.callbacks).length === 0) {
        websocketService.disconnect();
    }
};

const createEmptyTransactionsTable = (container) => {
    if (!container) return;
    
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
    <tbody></tbody>
    `;

    container.innerHTML = '';
    container.appendChild(table);
    
    // Add a message if no transactions yet
    const tbody = table.querySelector('tbody');
    const emptyRow = document.createElement('tr');
    emptyRow.className = 'empty-transactions-row';
    emptyRow.innerHTML = '<td colspan="5" class="info-message">Ожидание транзакций...</td>';
    tbody.appendChild(emptyRow);
};

const addTransactionToTable = (transaction, container) => {
    if (!transaction || !container) return;

    const table = container.querySelector('.transactions-table');
    if (!table) {
        createEmptyTransactionsTable(container);
        addTransactionToTable(transaction, container);
        return;
    }

    const tbody = table.querySelector('tbody');
    if (!tbody) {
        console.error('Не найден tbody в таблице транзакций');
        return;
    }

    // Remove empty message if it exists
    const emptyRow = tbody.querySelector('.empty-transactions-row');
    if (emptyRow) {
        emptyRow.remove();
    }

    // Check if transaction already exists in the table
    const rows = tbody.querySelectorAll('tr');
    for (const row of rows) {
        const idCell = row.querySelector('td:first-child');
        if (idCell && idCell.textContent === transaction.id) {
            return; // Transaction already exists
        }
    }

    try {
        const newRow = document.createElement('tr');

        const transactionDate = formatDate(transaction.transactionTimestamp);
        const accountFrom = transaction.accountFrom || 'Пополнение';
        const accountTo = transaction.accountTo || 'Нет данных';
        const currency = transaction.currency || 'RUB';
        const amount = formatCurrency(transaction.amount, currency);

        newRow.innerHTML = `
            <td>${transaction.id || 'Нет данных'}</td>
            <td>${transactionDate}</td>
            <td>${accountFrom}</td>
            <td>${accountTo}</td>
            <td>${amount}</td>
        `;

        // Add new transaction at the top of the table
        if (tbody.firstChild) {
            tbody.insertBefore(newRow, tbody.firstChild);
        } else {
            tbody.appendChild(newRow);
        }
    } catch (error) {
        console.error('Error adding transaction to table:', error);
    }
};