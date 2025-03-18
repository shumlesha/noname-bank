import {formatCurrency, getAccountStatus, getAccountType} from '../utils/format-utils.js';


export const renderAccountsList = (accounts, container, onAccountSelect) => {
    if (!accounts || !accounts.length) {
        container.innerHTML = '<div class="error-message-container"><p class="info-message">Счета не найдены</p></div>';
        return;
    }

    const table = document.createElement('table');
    table.className = 'accounts-table';

    table.innerHTML = `
    <thead>
      <tr>
        <th>Номер счета</th>
        <th>Клиент</th>
        <th>Баланс</th>
        <th>Тип</th>
        <th>Статус</th>
        <th>Действия</th>
      </tr>
    </thead>
    <tbody>
      ${accounts.map(account => {
        const status = getAccountStatus(account);
        const accountType = getAccountType(account);
        const number = account.number || 'Нет данных';
        const clientId = account.clientId || 'Нет данных';
        const balance = formatCurrency(account.balance);
        
        const clientIdDisplay = account.clientId 
            ? `<a href="/employee/client/${account.clientId}" class="client-id-link">${account.clientId}</a>` 
            : 'Нет данных';

        return `<tr>
          <td>${number}</td>
          <td>${clientIdDisplay}</td>
          <td>${balance}</td>
          <td>${accountType}</td>
          <td>${status}</td>
          <td>
            <button class="btn btn-details" data-account-id="${account.id}" data-client-id="${account.clientId}">
              Детали
            </button>
          </td>
        </tr>`;
    }).join('')}
    </tbody>
  `;

    container.innerHTML = '';
    container.appendChild(table);

    container.querySelectorAll('.btn-details').forEach(button => {
        button.addEventListener('click', function() {
            const accountId = this.getAttribute('data-account-id');
            const clientId = this.getAttribute('data-client-id');
            if (onAccountSelect && typeof onAccountSelect === 'function') {
                onAccountSelect(accountId, clientId);
            }
        });
    });
};
