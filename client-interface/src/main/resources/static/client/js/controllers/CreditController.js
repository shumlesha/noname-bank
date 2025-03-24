import {authService} from '../core/authService.js';
import {creditService} from '../core/creditService.js';
import {DomUtils} from '../utils/domUtils.js';
import {config} from '../config/config.js';
import {showError, showSuccess} from '../utils/modalUtils.js';

export class CreditController {
    constructor() {
        this.init();
    }
    
    init() {
        const userData = authService.getCurrentUser();
        
        this.displayUserData(userData);
        
        this.loadCredits();
        
        this.bindEventListeners();
    }
    
    displayUserData(userData) {
        if (!userData) return;
        
        const userEmailEl = DomUtils.find('#user-email');
        if (userEmailEl) userEmailEl.textContent = userData.email;
    }
    
    bindEventListeners() {
        DomUtils.on('#logout-btn', 'click', async () => {
            try {
                await authService.logout();
            } catch (error) {
                console.error("Ошибка при выходе:", error);
                showError("Произошла ошибка при выходе из системы.");
            }
        });
        
        DomUtils.on('#back-btn', 'click', () => {
            window.location.href = config.routes.home;
        });
        
        DomUtils.on('#fetch-tariffs-btn', 'click', async () => {
            await this.fetchCreditTariffs();
        });
        
        DomUtils.on('#close-modal-btn', 'click', () => {
            DomUtils.hideModal('tariff-modal');
        });
    }

    async loadCredits() {
        const clientId = authService.getCurrentUser()?.userId;
        if (!clientId) {
            showError("Ошибка: не удалось получить идентификатор клиента.");
            return;
        }

        try {
            const data = await creditService.loadCredits(clientId);
            let creditsList = DomUtils.find("#credits-list");
            creditsList.innerHTML = "";

            if (data.data.length === 0) {
                creditsList.innerHTML = "<tr><td colspan='7'>Нет активных кредитов</td></tr>";
                return;
            }

            let rows = data.data.map(credit => {
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

            DomUtils.findAll(".pay-btn").forEach(button => {
                button.addEventListener("click", () => {
                    this.payCredit(button.dataset.id);
                });
            });

        } catch (error) {
            console.error("Ошибка загрузки кредитов:", error);
            showError("Ошибка при загрузке кредитов.");
        }
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
                this.loadCredits();
            } catch (error) {
                showError("Ошибка при оплате кредита.");
            }
        });
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
            
            DomUtils.findAll(".take-credit-btn").forEach(button => {
                button.addEventListener("click", () => {
                    this.takeCredit(button.dataset.id);
                });
            });
            
            DomUtils.showModal('tariff-modal');
        } catch (error) {
            console.error("Ошибка загрузки тарифов:", error);
            showError("Ошибка при загрузке тарифов.");
        }
    }
    
    async takeCredit(tariffId) {
        const clientId = authService.getCurrentUser()?.userId;
        if (!clientId) {
            showError("Ошибка: не удалось получить идентификатор клиента.");
            return;
        }

        const modal = document.createElement('div');
        modal.className = 'modal show';
        modal.id = 'credit-amount-modal';
        modal.innerHTML = `
            <div class="modal-content">
                <span id="close-credit-amount-modal-btn" class="close">&times;</span>
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
        
        const closeBtn = document.getElementById('close-credit-amount-modal-btn');
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
                await creditService.takeCredit(clientId, tariffId, amount);
                showSuccess("Кредит успешно оформлен");
                await this.loadCredits();
                DomUtils.hideModal('tariff-modal');
            } catch (err) {
                showError("Ошибка при создании кредита: " + (err.message || "неизвестная ошибка"));
            }
        });
    }
}
