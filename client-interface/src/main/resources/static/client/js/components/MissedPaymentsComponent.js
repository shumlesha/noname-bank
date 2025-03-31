import {DomUtils} from '../utils/domUtils.js';
import {creditService} from '../core/creditService.js';
import {showError} from '../utils/modalUtils.js';

export class MissedPaymentsComponent {
    constructor(parentController) {
        this.parentController = parentController;
    }

    async loadMissedPayments(userId) {
        try {
            const response = await creditService.getMissedPayments(userId);
            const missedPaymentsList = DomUtils.find('#missed-payments-list');
            
            if (!response.data || response.data.length === 0) {
                missedPaymentsList.innerHTML = '<div class="no-missed-payments">У вас нет просроченных платежей</div>';
                return;
            }

            const paymentsHtml = response.data.map(payment => `
                <div class="missed-payment-item">
                    <div class="missed-payment-info">
                        <div class="missed-payment-credit-id">ID кредита: ${payment.creditId}</div>
                        <div class="missed-payment-date">Дата просрочки: ${new Date(payment.missedDate).toLocaleDateString()}</div>
                        <div class="missed-payment-amount">Сумма долга: ${payment.debt} ₽</div>
                        <div class="missed-payment-total">Общая сумма выплат: ${payment.amount} ₽</div>
                    </div>
                </div>
            `).join('');

            missedPaymentsList.innerHTML = paymentsHtml;
        } catch (error) {
            console.error("Ошибка при загрузке просроченных платежей:", error);
            showError("Ошибка при загрузке просроченных платежей");
        }
    }
} 