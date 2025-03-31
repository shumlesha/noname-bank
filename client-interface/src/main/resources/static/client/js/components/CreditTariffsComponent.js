import {DomUtils} from '../utils/domUtils.js';
import {creditService} from '../core/creditService.js';
import {showError, showSuccess} from '../utils/modalUtils.js';

export class CreditTariffsComponent {
    constructor(parentController) {
        this.parentController = parentController;
    }

    async fetchCreditTariffs() {
        try {
            const data = await creditService.fetchCreditTariffs();
            let tariffsList = DomUtils.find("#tariffs-list");
            tariffsList.innerHTML = "";
            
            if (data.data.length === 0) {
                tariffsList.innerHTML = "<tr><td colspan='3'>Нет доступных тарифов</td></tr>";
                return;
            }

            data.data.forEach(tariff => {
                let row = `<tr>
                    <td>${tariff.name}</td>
                    <td>${tariff.getFormattedInterestRate()}</td>
                    <td>${tariff.getFormattedAutoPaymentRate()}</td>
                    <td>${tariff.getFormattedPenaltyRate()}</td>
                    <td>
                        <button class="btn btn-success take-credit-btn" data-id="${tariff.id}">Выбрать</button>
                    </td>
                </tr>`;
                tariffsList.innerHTML += row;
            });
            
            DomUtils.showModal('tariff-modal');
            
            DomUtils.findAll(".take-credit-btn").forEach(button => {
                button.addEventListener("click", () => {
                    this.takeCredit(button.dataset.id);
                });
            });
        } catch (error) {
            console.error("Ошибка при загрузке тарифов:", error);
            showError("Ошибка при загрузке кредитных тарифов.");
        }
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