import {DomUtils} from '../utils/domUtils.js';
import {accountService} from '../core/accountService.js';
import {showConfirm, showError, showSuccess} from '../utils/modalUtils.js';
import {settingsService} from '../core/settingsService.js';
import {PaginationComponent} from './PaginationComponent.js';

export class AccountsComponent {
    constructor(parentController) {
        this.parentController = parentController;
        this.accounts = [];
        this.filters = {
            type: 'all',
            status: 'all'
        };
        this.pagination = new PaginationComponent('accounts-pagination', 5, () => this.renderAccounts());
    }

    async loadAccounts() {
        const userData = this.parentController.getUserData();
        if (!userData) return;

        try {
            const data = await accountService.loadAccounts(userData.userId);
            this.accounts = data.data;
            this.renderAccounts();
            return this.accounts;
        } catch (error) {
            console.error("Ошибка загрузки счетов:", error);
            showError("Не удалось загрузить счета");
        }
    }

    renderAccounts() {
        let accountsList = DomUtils.find("#accounts-list");
        accountsList.innerHTML = "";

        if (this.accounts.length === 0) {
            accountsList.innerHTML = "<tr><td colspan='6'>У вас нет счетов</td></tr>";
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
            accountsList.innerHTML = "<tr><td colspan='6'>Нет счетов, соответствующих выбранным фильтрам</td></tr>";
            return;
        }

        this.pagination.setTotalItems(filteredAccounts.length);
        
        const paginatedAccounts = this.pagination.getPaginatedItems(filteredAccounts);

        paginatedAccounts.forEach(account => {
            const isHidden = settingsService.isAccountHidden(account.id);
            const rowClass = isHidden ? 'hidden-account' : '';
            const balanceClass = isHidden ? 'hidden-account-value' : '';
            
            let actions = account.isActive()
                ? `<div class="account-actions">
                <button class="btn btn-primary deposit-btn" data-id="${account.id}">Пополнить</button>
                <button class="btn btn-secondary withdraw-btn" data-id="${account.id}">Снять</button>
                <button class="btn btn-danger close-btn" data-id="${account.id}">Закрыть</button>
            </div>`
                : `<span>Нет доступных действий</span>`;

            const visibilityIcon = isHidden ? 'visibility_off' : 'visibility';
            const visibilityTitle = isHidden ? 'Показать счет' : 'Скрыть счет';

            let row = `<tr class="${rowClass}">
            <td>
                <div style="display: flex; align-items: center; justify-content: space-between">
                    <a href="#" class="account-link" data-id="${account.id}">${account.id}</a>
                    <button class="account-visibility-toggle" data-id="${account.id}" title="${visibilityTitle}">
                        <span class="material-icons">${visibilityIcon}</span>
                    </button>
                </div>
            </td>
            <td class="${balanceClass}">${account.getFormattedBalance()}</td>
            <td>${account.currency}</td>
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
                this.parentController.loadAccountDetails(link.dataset.id);
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

    async createAccount(currency) {
        const userData = this.parentController.getUserData();
        if (!userData) return;

        try {
            await accountService.createAccount(userData.userId, currency);
            showSuccess(`Счет в валюте ${currency} успешно создан!`);
            setTimeout(async () => {
                await this.loadAccounts();
            }, 1000);
        } catch (error) {
            console.error("Ошибка при создании:", error);
            showError("Ошибка при создании счета.");
        }
    }

    async closeAccount(accountId) {
        const confirmed = await showConfirm("Вы уверены, что хотите закрыть счет?");
        if (!confirmed) {
            return;
        }
        
        const userData = this.parentController.getUserData();
        if (!userData) return;
        
        try {
            await accountService.closeAccount(userData.userId, accountId);
            showSuccess("Счет успешно закрыт");
            setTimeout(async () => {
                await this.loadAccounts();
            }, 1000);
        } catch (error) {
            console.error("Ошибка при закрытии:", error);
            showError("Ошибка при закрытии счета.");
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

    async processDeposit(amount, accountId) {
        try {
            await accountService.depositMoney(amount, accountId);
            showSuccess("Счет успешно пополнен");
            setTimeout(async () => {
                await this.loadAccounts();
            }, 1000);
        } catch (error) {
            console.error("Ошибка при пополнении:", error);
            showError("Ошибка при пополнении счета.");
        }
    }
    
    async processWithdraw(amount, accountId) {
        try {
            await accountService.withdrawMoney(amount, accountId);
            showSuccess("Средства успешно сняты");
            setTimeout(async () => {
                await this.loadAccounts();
            }, 1000);
        } catch (error) {
            console.error("Ошибка при снятии:", error);
            showError("Ошибка при снятии средств.");
        }
    }

    toggleAccountVisibility(accountId) {
        const userData = this.parentController.getUserData();
        if (!userData) return;
        
        const isHidden = settingsService.isAccountHidden(accountId);
        
        if (isHidden) {
            settingsService.unhideAccountLocally(accountId);
        } else {
            settingsService.hideAccountLocally(accountId);
        }

        this.renderAccounts();

        const action = isHidden 
            ? settingsService.syncUnhideAccountWithServer(userData.userId, accountId)
            : settingsService.syncHideAccountWithServer(userData.userId, accountId);
            
        action.catch(error => {
            console.error("Ошибка при синхронизации видимости счета с сервером:", error);
        });
    }

    setFilters(type, status) {
        this.filters.type = type;
        this.filters.status = status;
    }

    getAccounts() {
        return this.accounts;
    }
} 