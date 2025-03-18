import {formatCurrency, formatDate} from '../utils/format-utils.js';


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
            const amount = formatCurrency(tx.amount);

            return `<tr>
              <td>${tx.id || 'Нет данных'}</td>
              <td>${transactionDate}</td>
              <td>${accountFrom}</td>
              <td>${accountTo}</td>
              <td>${amount}</td>
            </tr>`;
        } catch (error) {
            console.error('Error rendering transaction:', tx, error);
            return `<tr><td colspan="5">Ошибка отображения транзакции</td></tr>`;
        }
    }).join('')}
    </tbody>
  `;

    container.innerHTML = '<h3>Транзакции по счету</h3>';
    container.appendChild(table);
};


export const normalizeTransactionsResponse = (response) => {
    if (!response) return [];

    if (response.transactions && Array.isArray(response.transactions)) {
        return response.transactions;
    }

    if (response.data && response.data.transactions && Array.isArray(response.data.transactions)) {
        return response.data.transactions;
    }

    if (Array.isArray(response)) {
        return response;
    }

    console.error('Неожиданный формат данных транзакций:', response);
    return [];
};