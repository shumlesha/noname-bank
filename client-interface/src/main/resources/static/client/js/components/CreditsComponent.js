import {DomUtils} from '../utils/domUtils.js';
import {creditService} from '../core/creditService.js';
import {showError, showSuccess} from '../utils/modalUtils.js';
import {PaginationComponent} from './PaginationComponent.js';

export class CreditsComponent {
    constructor(parentController) {
        this.parentController = parentController;
        this.credits = [];
        this.pagination = new PaginationComponent('credits-pagination', 5, () => this.renderCredits());
    }

    async loadCredits(clientId) {
        try {
            const data = await creditService.loadCredits(clientId);
            this.credits = data.data;
            this.renderCredits();
            return this.credits;
        } catch (error) {
            console.error("Ошибка загрузки кредитов:", error);
            showError("Ошибка при загрузке кредитов.");
        }
    }
    
    renderCredits() {
        let creditsList = DomUtils.find("#credits-list");
        creditsList.innerHTML = "";

        if (this.credits.length === 0) {
            creditsList.innerHTML = "<tr><td colspan='8'>Нет активных кредитов</td></tr>";
            return;
        }

        this.pagination.setTotalItems(this.credits.length);

        const paginatedCredits = this.pagination.getPaginatedItems(this.credits);

        let rows = paginatedCredits.map(credit => {
            let actions = credit.status === "PAID_OFF"
                ? "<span>Нет доступных действий</span>"
                : `<button class="btn btn-primary pay-btn" data-id="${credit.id}">Оплатить</button>`;

            return `<tr>
            <td>${credit.id}</td>
            <td>${credit.getFormattedAmount()}</td>
            <td>${credit.getFormattedPaidAmount()}</td>
            <td>${credit.tariffName}</td>
            <td>${credit.getFormattedInterestRate()}</td>
            <td>${credit.getStatusLabel()}</td>
            <td>${credit.nextPaymentDate}</td>
            <td>${actions}</td>
        </tr>`;
        }).join("");

        creditsList.innerHTML = rows;

        this.bindPayButtons();
    }
    
    bindPayButtons() {
        DomUtils.findAll(".pay-btn").forEach(button => {
            button.addEventListener("click", () => {
                this.payCredit(button.dataset.id);
            });
        });
    }

    async payCredit(creditId) {
        const modal = document.createElement('div');
        modal.className = 'modal show';
        modal.id = 'payment-modal';
        modal.innerHTML = `
            <div class="modal-content">
                <span id="close-payment-modal-btn" class="close">&times;</span>
                <h3>Оплата кредита</h3>
                <div class="modal-form">
                    <div class="form-group">
                        <label for="payment-amount">Введите сумму оплаты:</label>
                        <input type="number" id="payment-amount" class="form-control" min="1" step="1">
                    </div>
                    <button id="confirm-payment-btn" class="btn btn-primary">Оплатить</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        const closeBtn = document.getElementById('close-payment-modal-btn');
        const confirmBtn = document.getElementById('confirm-payment-btn');
        const amountInput = document.getElementById('payment-amount');
        
        const closeModal = () => {
            document.body.removeChild(modal);
        };
        
        closeBtn.addEventListener('click', closeModal);
        
        confirmBtn.addEventListener('click', async () => {
            const amount = amountInput.value;
            if (!amount) return;
            
            closeModal();
            
            try {
                await creditService.payCredit(creditId, amount);
                showSuccess("Платеж успешно выполнен");
                const userData = this.parentController.getUserData();
                await this.loadCredits(userData.userId);
            } catch (error) {
                showError("Ошибка при оплате кредита.");
            }
        });
    }
} 