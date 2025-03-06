import { authService } from '../core/authService.js';
import { accountService } from '../core/accountService.js';
import { DomUtils } from '../utils/domUtils.js';
import { config } from '../config/config.js';

export class HomeController {
    constructor() {
        this.accounts = [];
        this.filters = {
            type: 'all',
            status: 'all'
        };
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

        DomUtils.on('#close-account-modal-btn', 'click', () => {
            DomUtils.hideModal('account-modal');
        });

        DomUtils.on('#account-modal-submit', 'click', () => {
            const amount = DomUtils.find('#amount').value;
            const accountId = DomUtils.find('#account-modal-submit').dataset.accountId;
            const action = DomUtils.find('#account-modal-submit').dataset.action;
            
            if (action === 'deposit') {
                this.processDeposit(amount, accountId);
            } else if (action === 'withdraw') {
                this.processWithdraw(amount, accountId);
            }
            
            DomUtils.hideModal('account-modal');
        });

        // Add event listeners for filters
        DomUtils.on('#account-type-filter', 'change', (e) => {
            this.filters.type = e.target.value;
        });

        DomUtils.on('#account-status-filter', 'change', (e) => {
            this.filters.status = e.target.value;
        });

        DomUtils.on('#apply-filters-btn', 'click', () => {
            this.renderAccounts();
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
            alert("Счет создан!");
            this.loadAccounts();
        } catch (error) {
            alert("Ошибка при создании счета.");
        }
    }

    async loadAccounts() {
        console.log("Вызов загрузки счетов");
        const userData = authService.getCurrentUser();
        if (!userData) {
            alert("Ошибка: не удалось получить данные пользователя.");
            return;
        }

        try {
            const data = await accountService.loadAccounts(userData.userId);
            this.accounts = data.data;
            this.renderAccounts();
        } catch (error) {
            console.error("Ошибка загрузки счетов:", error);
        }
    }

    renderAccounts() {
        let accountsList = DomUtils.find("#accounts-list");
        accountsList.innerHTML = "";

        if (this.accounts.length === 0) {
            accountsList.innerHTML = "<tr><td colspan='5'>У вас нет счетов</td></tr>";
            return;
        }

        const filteredAccounts = this.accounts.filter(account => {
            if (this.filters.type !== 'all') {
                if (this.filters.type === 'credit' && !account.isCredit) return false;
                if (this.filters.type === 'debit' && account.isCredit) return false;
            }

            if (this.filters.status !== 'all') {
                if (this.filters.status === 'active' && !account.isActive()) return false;
                if (this.filters.status === 'closed' && account.isActive()) return false;
            }
            
            return true;
        });

        if (filteredAccounts.length === 0) {
            accountsList.innerHTML = "<tr><td colspan='5'>Нет счетов, соответствующих выбранным фильтрам</td></tr>";
            return;
        }

        filteredAccounts.forEach(account => {
            let actions = account.isActive()
                ? `<div class="account-actions">
                <button class="btn btn-primary deposit-btn" data-id="${account.id}">Пополнить</button>
                <button class="btn btn-secondary withdraw-btn" data-id="${account.id}">Снять</button>
                <button class="btn btn-danger close-btn" data-id="${account.id}">Закрыть</button>
            </div>`
                : `<span>Нет доступных действий</span>`;

            let row = `<tr>
            <td><a href="#" class="account-link" data-id="${account.id}">${account.number}</a></td>
            <td>${account.getFormattedBalance()}</td>
            <td>${account.getType()}</td>
            <td>${account.getStatus()}</td>
            <td>${actions}</td>
        </tr>`;

            accountsList.innerHTML += row;
        });

        this.bindAccountLinks();
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
                <p><strong>Номер счета:</strong> ${data.account.number}</p>
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
            alert("Ошибка загрузки деталей счета.");
        }
    }
    
    async depositMoney(accountId) {
        DomUtils.find('#account-modal-title').textContent = 'Пополнение счета';
        DomUtils.find('#amount').value = '';
        const submitBtn = DomUtils.find('#account-modal-submit');
        submitBtn.dataset.accountId = accountId;
        submitBtn.dataset.action = 'deposit';
        DomUtils.showModal('account-modal');
    }
    
    async withdrawMoney(accountId) {
        DomUtils.find('#account-modal-title').textContent = 'Снятие со счета';
        DomUtils.find('#amount').value = '';
        const submitBtn = DomUtils.find('#account-modal-submit');
        submitBtn.dataset.accountId = accountId;
        submitBtn.dataset.action = 'withdraw';
        DomUtils.showModal('account-modal');
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
            this.loadAccounts();
        } catch (error) {
            alert("Ошибка при закрытии счета.");
        }
    }
    
    async processDeposit(amount, accountId) {
        try {
            await accountService.depositMoney(amount, accountId);
            this.loadAccounts();
        } catch (error) {
            alert("Ошибка при пополнении счета.");
        }
    }
    
    async processWithdraw(amount, accountId) {
        try {
            await accountService.withdrawMoney(amount, accountId);
            this.loadAccounts();
        } catch (error) {
            alert("Ошибка при снятии средств.");
        }
    }
}
