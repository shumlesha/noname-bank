import {formatCurrency} from '../utils/format-utils.js';

export const renderMissedPaymentsList = (missedPayments, container) => {
    if (!missedPayments || missedPayments.length === 0) {
        container.innerHTML = '<div class="error-message-container"><p class="info-message">Просроченные платежи не найдены</p></div>';
        return;
    }

    const card = document.createElement('div');
    card.className = 'missed-payments-card';

    card.innerHTML = `
        <h3>Просроченные платежи</h3>
        <div class="missed-payments-summary">
            <div class="summary-item">
                <span class="summary-label">Количество просрочек:</span>
                <span class="summary-value">${missedPayments.length}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">Общая сумма долга:</span>
                <span class="summary-value">${formatCurrency(calculateTotalDebt(missedPayments))}</span>
            </div>
        </div>
        <table class="missed-payments-list">
            <thead>
                <tr>
                    <th>ID кредита</th>
                    <th>Дата просрочки</th>
                    <th>Сумма платежа</th>
                    <th>Сумма долга</th>
                </tr>
            </thead>
            <tbody>
                ${missedPayments.map(payment => `
                <tr>
                    <td>${payment.creditId}</td>
                    <td>${formatDate(payment.missedDate)}</td>
                    <td>${formatCurrency(payment.amount)}</td>
                    <td class="debt-amount">${formatCurrency(payment.debt)}</td>
                </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    container.innerHTML = '';
    container.appendChild(card);
};

const calculateTotalDebt = (missedPayments) => {
    return missedPayments.reduce((total, payment) => total + parseFloat(payment.debt), 0);
};

const formatDate = (dateString) => {
    if (!dateString) return 'Н/Д';
    
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString;
    
    return new Intl.DateTimeFormat('ru-RU', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    }).format(date);
};
