import {formatCurrency, formatDate, getAccountStatus, getAccountType} from '../utils/format-utils.js';


export const renderAccountDetails = (account, container) => {
    if (!account || typeof account !== 'object') {
        container.innerHTML = '<p class="error-message">Некорректные данные счета</p>';
        return;
    }

    const status = getAccountStatus(account);
    const accountType = getAccountType(account);
    const creationDate = formatDate(account.creationTimestamp);
    const blockedDate = account.blockedTimestamp ? formatDate(account.blockedTimestamp) : 'Нет';
    const closedDate = account.closedTimestamp ? formatDate(account.closedTimestamp) : 'Нет';

    const detailsHTML = `
    <h3>Детали счета</h3>
    <div class="account-info">
      <p><strong>ID счета:</strong> ${account.id || 'Нет данных'}</p>
      <p><strong>Номер счета:</strong> ${account.number || 'Нет данных'}</p>
      <p><strong>ID клиента:</strong> ${account.clientId || 'Нет данных'}</p>
      <p><strong>Баланс:</strong> ${formatCurrency(account.balance)}</p>
      <p><strong>Тип счета:</strong> ${accountType}</p>
      <p><strong>Статус:</strong> ${status}</p>
      <p><strong>Дата открытия:</strong> ${creationDate}</p>
      <p><strong>Дата блокировки:</strong> ${blockedDate}</p>
      <p><strong>Дата закрытия:</strong> ${closedDate}</p>
    </div>
  `;

    container.innerHTML = detailsHTML;
};