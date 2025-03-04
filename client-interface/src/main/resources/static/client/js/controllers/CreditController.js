import { authService } from '../core/authService.js';
import { creditService } from '../core/creditService.js';
import { DomUtils } from '../utils/domUtils.js';
import { config } from '../config/config.js';

export class CreditController {
    constructor() {
        this.init();
    }
    
    init() {
        if (!authService.isAuthenticated()) {
            window.location.href = config.routes.login;
            return;
        }
        
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
                window.location.href = config.routes.login;
            } catch (error) {
                console.error("Ошибка при выходе:", error);
                alert("Произошла ошибка при выходе из системы.");
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
            alert("Ошибка: не удалось получить идентификатор клиента.");
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
            
            data.data.forEach(credit => {
                let row = `<tr>
                    <td>${credit.id}</td>
                    <td>${credit.getFormattedAmount()}</td>
                    <td>${credit.getFormattedPaidAmount()}</td>
                    <td>${credit.tariffName}</td>
                    <td>${credit.getFormattedInterestRate()}</td>
                    <td>${credit.status}</td>
                    <td>
                        <button class="btn btn-primary pay-btn" data-id="${credit.id}">Оплатить</button>
                    </td>
                </tr>`;
                creditsList.innerHTML += row;
            });
            
            DomUtils.findAll(".pay-btn").forEach(button => {
                button.addEventListener("click", () => {
                    this.payCredit(button.dataset.id);
                });
            });
            
        } catch (error) {
            console.error("Ошибка загрузки кредитов:", error);
        }
    }
    
    async payCredit(creditId) {
        let amount = prompt("Введите сумму оплаты:");
        if (!amount) return;
        
        try {
            await creditService.payCredit(creditId, amount);
            this.loadCredits();
        } catch (error) {
            alert("Ошибка при оплате кредита.");
        }
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
        }
    }
    
    async takeCredit(tariffId) {
        const clientId = authService.getCurrentUser()?.userId;
        if (!clientId) {
            alert("Ошибка: не удалось получить идентификатор клиента.");
            return;
        }
        
        let amount = prompt("Введите сумму кредита:");
        if (!amount) return;
        
        try {
            await creditService.takeCredit(clientId, tariffId, amount);
            await this.loadCredits();
            DomUtils.hideModal('tariff-modal');
        } catch (err) {
            alert("Ошибка при создании кредита: " + err.message);
        }
    }
}
