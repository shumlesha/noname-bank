import {DomUtils} from '../utils/domUtils.js';
import {creditService} from '../core/creditService.js';
import {showError, showSuccess} from '../utils/modalUtils.js';
import {PaginationComponent} from './PaginationComponent.js';

export class CreditTariffsComponent {
    constructor(parentController) {
        this.parentController = parentController;
        this.tariffs = [];
        this.pagination = new PaginationComponent('tariffs-pagination', 5, () => this.renderTariffs());
    }

    async fetchCreditTariffs() {
        try {
            const data = await creditService.fetchCreditTariffs();
            this.tariffs = data.data || [];
            this.renderTariffs();
            DomUtils.showModal('tariff-modal');
        } catch (error) {
            console.error("Ошибка при загрузке тарифов:", error);
            showError("Ошибка при загрузке кредитных тарифов.");
        }
    }

    renderTariffs() {
        let tariffsList = DomUtils.find("#tariffs-list");
        tariffsList.innerHTML = "";
        
        if (this.tariffs.length === 0) {
            tariffsList.innerHTML = "<tr><td colspan='5'>Нет доступных тарифов</td></tr>";
            return;
        }

        this.pagination.setTotalItems(this.tariffs.length);

        const paginatedTariffs = this.pagination.getPaginatedItems(this.tariffs);

        let rows = paginatedTariffs.map(tariff => `
            <tr>
                <td>${tariff.name}</td>
                <td>${tariff.description}</td>
                <td>${tariff.interestRate}%</td>
                <td><button class="btn btn-primary take-credit-btn" data-id="${tariff.id}">Оформить</button></td>
            </tr>
        `).join("");

        tariffsList.innerHTML = rows;
        
        DomUtils.findAll(".take-credit-btn").forEach(button => {
            button.addEventListener("click", () => {
                this.takeCredit(button.dataset.id);
            });
        });
    }
    
    async takeCredit(tariffId) {
        DomUtils.hideModal('tariff-modal');
        
        const modal = document.createElement('div');
        modal.className = 'modal show';
        modal.id = 'credit-form-modal';
        modal.innerHTML = `
            <div class="modal-content">
                <span id="close-credit-form-modal-btn" class="close">&times;</span>
                <h3>Оформление кредита</h3>
                <div class="modal-form">
                    <div class="form-group">
                        <label for="credit-amount">Введите сумму кредита:</label>
                        <input type="number" id="credit-amount" class="form-control" min="1000" step="1000">
                    </div>
                    <button id="confirm-credit-btn" class="btn btn-primary">Оформить</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        const closeBtn = document.getElementById('close-credit-form-modal-btn');
        const confirmBtn = document.getElementById('confirm-credit-btn');
        const amountInput = document.getElementById('credit-amount');
        
        const closeModal = () => {
            document.body.removeChild(modal);
        };
        
        closeBtn.addEventListener('click', closeModal);
        
        confirmBtn.addEventListener('click', async () => {
            const amount = amountInput.value;
            if (!amount) return;
            
            closeModal();
            
            try {
                const userData = this.parentController.getUserData();
                await creditService.takeCredit(userData.userId, tariffId, amount);
                showSuccess("Кредит успешно оформлен");
                await this.parentController.reloadCredits();
            } catch (error) {
                console.error("Ошибка при оформлении кредита:", error);
                showError("Ошибка при оформлении кредита.");
            }
        });
    }
} 