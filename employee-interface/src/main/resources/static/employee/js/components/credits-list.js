import { formatCurrency } from '../utils/format-utils.js';

export const renderCreditsList = (credits, container) => {
    if (!credits || credits.length === 0) {
        container.innerHTML = '<div class="error-message-container"><p class="info-message">Кредиты не найдены</p></div>';
        return;
    }

    const table = document.createElement('table');
    table.className = 'credits-list';

    table.innerHTML = `
    <thead>
        <tr>
            <th>ID кредита</th>
            <th>Сумма кредита</th>
            <th>Выплачено</th>
            <th>Тариф</th>
            <th>Процентная ставка</th>
            <th>Статус</th>
        </tr>
    </thead>
    <tbody>
        ${credits.map(credit => {
            const statusClass = getStatusClass(credit.status);
            const statusText = getStatusText(credit.status);
            
            return `
            <tr>
                <td>${credit.id}</td>
                <td>${formatCurrency(credit.amount)}</td>
                <td>${formatCurrency(credit.paidAmount)}</td>
                <td>${credit.tariffName || 'Не указан'}</td>
                <td>${credit.interestRate ? credit.interestRate + '%' : 'Не указана'}</td>
                <td><span class="credit-status ${statusClass}">${statusText}</span></td>
            </tr>`;
        }).join('')}
    </tbody>`;

    container.innerHTML = '';
    container.appendChild(table);
};

const getStatusClass = (status) => {
    if (!status) return '';
    
    switch (status) {
        case 'ACTIVE':
            return 'active';
        case 'CLOSED':
            return 'closed';
        case 'OVERDUE':
            return 'overdue';
        default:
            return '';
    }
};

const getStatusText = (status) => {
    if (!status) return 'Неизвестно';
    
    switch (status) {
        case 'ACTIVE':
            return 'Активен';
        case 'CLOSED':
            return 'Закрыт';
        case 'OVERDUE':
            return 'Просрочен';
        default:
            return status;
    }
};
