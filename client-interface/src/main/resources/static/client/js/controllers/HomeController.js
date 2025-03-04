import { authService } from '../core/authService.js';
import { accountService } from '../core/accountService.js';
import { DomUtils } from '../utils/domUtils.js';
import { config } from '../config/config.js';

export class HomeController {
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
        
        this.loadAccounts();
        
        this.bindEventListeners();
    }
    
    displayUserData(userData) {
        if (!userData) return;
        
        const userEmailEl = DomUtils.find('#user-email');
        const userIdEl = DomUtils.find('#user-id');
        const userEmailDetailsEl = DomUtils.find('#user-email-details');
        
        if (userEmailEl) userEmailEl.textContent = userData.email;
        if (userIdEl) userIdEl.textContent = userData.userId;
        if (userEmailDetailsEl) userEmailDetailsEl.textContent = userData.email;
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
        
        DomUtils.on('#create-account-btn', 'click', () => {
            this.createAccount();
        });
        
        DomUtils.on('#credits-btn', 'click', () => {
            window.location.href = config.routes.credit;
        });
        
        DomUtils.on('#back-to-accounts-btn', 'click', () => {
            DomUtils.find('#accounts-section').style.display = 'block';
            DomUtils.find('#account-details-section').style.display = 'none';
        });
    }
    
    async createAccount() {
        const userData = authService.getCurrentUser();
        if (!userData) {
            alert("Ошибка: не удалось получить данные пользователя.");
            return;
        }
        
        try {
            await accountService.createAccount(userData.userId);
            await this.loadAccounts();
        } catch (error) {
            alert("Ошибка при создании счета.");
        }
    }
    
    async loadAccounts() {
        const userData = authService.getCurrentUser();
        if (!userData) {
            alert("Ошибка: не удалось получить данные пользователя.");
            return;
        }
        
        try {
            const data = await accountService.loadAccounts(userData.userId);
            let accountsList = DomUtils.find("#accounts-list");
            accountsList.innerHTML = "";
            
            if (data.data.length === 0) {
                accountsList.innerHTML = "<tr><td colspan='5'>У вас нет счетов</td></tr>";
                return;
            }
            
            data.data.forEach(account => {
                let row = `<tr>
                    <td><a href="#" class="account-link" data-id="${account.id}">${account.id}</a></td>
                    <td>${account.getFormattedBalance()}</td>
                    <td>${account.getType()}</td>
                    <td>${account.getStatus()}</td>
                    <td>
                        <button class="btn btn-primary deposit-btn" data-id="${account.id}">Пополнить</button>
                        <button class="btn btn-secondary withdraw-btn" data-id="${account.id}">Снять</button>
                        <button class="btn btn-danger close-btn" data-id="${account.id}">Закрыть</button>
                    </td>
                </tr>`;
                accountsList.innerHTML += row;
            });
            
            this.bindAccountLinks();
            
        } catch (error) {
            console.error("Ошибка загрузки счетов:", error);
        }
    }
    
    bindAccountLinks() {
        DomUtils.findAll(".account-link").forEach(link => {
            link.addEventListener("click", (e) => {
                e.preventDefault();
                this.loadAccountDetails(link.dataset.id);
            });
        });
        
        DomUtils.findAll(".deposit-btn").forEach(button => {
            button.addEventListener("click", () => {
                this.depositMoney(button.dataset.id);
            });
        });
        
        DomUtils.findAll(".withdraw-btn").forEach(button => {
            button.addEventListener("click", () => {
                this.withdrawMoney(button.dataset.id);
            });
        });
        
        DomUtils.findAll(".close-btn").forEach(button => {
            button.addEventListener("click", () => {
                this.closeAccount(button.dataset.id);
            });
        });
    }
    
    async loadAccountDetails(accountId) {
        const userData = authService.getCurrentUser();
        if (!userData) {
            alert("Ошибка: не удалось получить данные пользователя.");
            return;
        }
        
        try {
            const data = await accountService.loadAccountDetails(userData.userId, accountId);
            
            const accountDetailsEl = DomUtils.find("#account-details");
            const transactionsList = DomUtils.find("#transactions-list");
            
            accountDetailsEl.innerHTML = `
                <h3>Детали счета</h3>
                <p><strong>Номер счета:</strong> ${data.account.id}</p>
                <p><strong>Баланс:</strong> ${data.account.getFormattedBalance()}</p>
                <p><strong>Тип:</strong> ${data.account.getType()}</p>
                <p><strong>Статус:</strong> ${data.account.getStatus()}</p>
            `;
            
            transactionsList.innerHTML = "";
            
            if (data.transactions.length === 0) {
                transactionsList.innerHTML = "<tr><td colspan='3'>Нет транзакций</td></tr>";
            } else {
                data.transactions.forEach(transaction => {
                    let row = `<tr>
                        <td>${transaction.transactionTimestamp}</td>
                        <td>${transaction.getFormattedAmount(accountId)}</td>
                        <td>${transaction.getType(accountId)}</td>
                    </tr>`;
                    transactionsList.innerHTML += row;
                });
            }
            
            DomUtils.find('#accounts-section').style.display = 'none';
            DomUtils.find('#account-details-section').style.display = 'block';
            
        } catch (error) {
            console.error("Ошибка загрузки деталей счета:", error);
        }
    }
    
    async depositMoney(accountId) {
        let amount = prompt("Введите сумму пополнения:");
        if (!amount) return;
        
        try {
            await accountService.depositMoney(amount, accountId);
            await this.loadAccounts();
        } catch (error) {
            alert("Ошибка при пополнении счета.");
        }
    }
    
    async withdrawMoney(accountId) {
        let amount = prompt("Введите сумму снятия:");
        if (!amount) return;
        
        try {
            await accountService.withdrawMoney(amount, accountId);
            await this.loadAccounts();
        } catch (error) {
            alert("Ошибка при снятии средств.");
        }
    }
    
    async closeAccount(accountId) {
        if (!confirm("Вы уверены, что хотите закрыть счет?")) {
            return;
        }
        
        const userData = authService.getCurrentUser();
        if (!userData) {
            alert("Ошибка: не удалось получить данные пользователя.");
            return;
        }
        
        try {
            await accountService.closeAccount(userData.userId, accountId);
            await this.loadAccounts();
        } catch (error) {
            alert("Ошибка при закрытии счета.");
        }
    }
}
