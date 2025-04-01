import {DomUtils} from '../utils/domUtils.js';
import {creditService} from '../core/creditService.js';
import {showError} from '../utils/modalUtils.js';
import {PaginationComponent} from './PaginationComponent.js';

export class MissedPaymentsComponent {
    constructor(parentController) {
        this.parentController = parentController;
        this.missedPayments = [];
        this.pagination = new PaginationComponent('missed-payments-pagination', 2, () => this.renderMissedPayments());
    }

    async loadMissedPayments(userId) {
        try {
            const response = await creditService.getMissedPayments(userId);
            this.missedPayments = response.data || [];
            this.renderMissedPayments();
        } catch (error) {
            console.error("Ошибка при загрузке просроченных платежей:", error);
            showError("Ошибка при загрузке просроченных платежей");
        }
    }

    renderMissedPayments() {
        const missedPaymentsList = DomUtils.find('#missed-payments-list');
        
        if (!this.missedPayments || this.missedPayments.length === 0) {
            missedPaymentsList.innerHTML = '<div class="no-missed-payments">У вас нет просроченных платежей</div>';

            const paginationContainer = DomUtils.find('#missed-payments-pagination');
            if (paginationContainer) {
                paginationContainer.innerHTML = '';
            }
            return;
        }

        this.pagination.setTotalItems(this.missedPayments.length);

        const paginatedPayments = this.pagination.getPaginatedItems(this.missedPayments);

        const paymentsHtml = paginatedPayments.map(payment => `
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
    }
} 