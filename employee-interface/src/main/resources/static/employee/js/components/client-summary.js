import {formatCurrency} from '../utils/format-utils.js';

export const renderClientSummary = (accounts, credits, container) => {
    if (!container) return;
    
    const summaryContainer = document.createElement('div');
    summaryContainer.className = 'client-summary';

    const totalAccounts = accounts ? accounts.length : 0;
    let totalBalance = 0;
    
    if (accounts && accounts.length > 0) {
        accounts.forEach(account => {
            if (account.balance) {
                totalBalance += parseFloat(account.balance);
            }
        });
    }

    const totalCredits = credits ? credits.length : 0;
    let totalCreditAmount = 0;
    let totalPaidAmount = 0;
    let activeCredits = 0;
    
    if (credits && credits.length > 0) {
        credits.forEach(credit => {
            if (credit.amount) {
                totalCreditAmount += parseFloat(credit.amount);
            }
            if (credit.paidAmount) {
                totalPaidAmount += parseFloat(credit.paidAmount);
            }
            if (credit.status === 'ACTIVE') {
                activeCredits++;
            }
        });
    }
    
    const remainingDebt = totalCreditAmount - totalPaidAmount;

    summaryContainer.innerHTML = `
        <h3>Сводная информация</h3>
        <div class="summary-grid">
            <div class="summary-item">
                <span class="summary-label">Всего счетов:</span>
                <span class="summary-value">${totalAccounts}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">Общий баланс:</span>
                <span class="summary-value">${formatCurrency(totalBalance)}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">Всего кредитов:</span>
                <span class="summary-value">${totalCredits}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">Активных кредитов:</span>
                <span class="summary-value">${activeCredits}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">Общая сумма кредитов:</span>
                <span class="summary-value">${formatCurrency(totalCreditAmount)}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">Выплачено по кредитам:</span>
                <span class="summary-value">${formatCurrency(totalPaidAmount)}</span>
            </div>
            <div class="summary-item">
                <span class="summary-label">Остаток задолженности:</span>
                <span class="summary-value">${formatCurrency(remainingDebt)}</span>
            </div>
        </div>
    `;
    
    container.appendChild(summaryContainer);
};
