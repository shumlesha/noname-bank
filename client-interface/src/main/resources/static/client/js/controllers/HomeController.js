import {authService} from '../core/authService.js';
import {DomUtils} from '../utils/domUtils.js';
import {config} from '../config/config.js';
import {storageService} from "../core/storageService.js";
import {settingsService} from "../core/settingsService.js";

import {AccountsComponent} from '../components/AccountsComponent.js';
import {AccountDetailsComponent} from '../components/AccountDetailsComponent.js';
import {TransferComponent} from '../components/TransferComponent.js';
import {ThemeComponent} from '../components/ThemeComponent.js';

export class HomeController {
    constructor() {

        settingsService.loadCachedSettings();

        this.accountsComponent = new AccountsComponent(this);
        this.accountDetailsComponent = new AccountDetailsComponent(this);
        this.transferComponent = new TransferComponent(this);
        this.themeComponent = new ThemeComponent(this);
        
        this.themeComponent.applyCurrentTheme();
        
        this.init();
    }
    
    init() {
        authService.loadUserData().then(async () => {
            const userData = this.getUserData();
            this.displayUserData(userData);

            const settingsPromise = this.loadUserSettings(userData.userId);
            const accountsPromise = this.loadAccounts();

            await Promise.all([settingsPromise, accountsPromise]);

            this.themeComponent.applyCurrentTheme();
            
            this.bindEventListeners();
        });
    }
    
    async loadUserSettings(userId) {
        try {
            await settingsService.loadUserSettings(userId);
        } catch (error) {
            console.error("Ошибка при загрузке настроек:", error);
        }
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
                window.location.href = "/";
                storageService.removeUserData()
            } catch (error) {
                console.error("Ошибка при выходе:", error);
            }
        });
        
        DomUtils.on('#create-account-btn', 'click', () => {
            DomUtils.showModal('create-account-modal');
        });
        
        DomUtils.on('#close-create-account-modal-btn', 'click', () => {
            DomUtils.hideModal('create-account-modal');
        });
        
        DomUtils.on('#confirm-create-account-btn', 'click', () => {
            const currency = DomUtils.find('#currency-select').value;
            DomUtils.hideModal('create-account-modal');
            this.accountsComponent.createAccount(currency);
        });
        
        DomUtils.on('#transfer-money-btn', 'click', () => {
            this.transferComponent.showTransferModal(this.accountsComponent.getAccounts());
        });
        
        DomUtils.on('#close-transfer-modal-btn', 'click', () => {
            DomUtils.hideModal('transfer-modal');
        });
        
        DomUtils.on('#confirm-transfer-btn', 'click', () => {
            this.transferComponent.processTransfer();
        });
        
        DomUtils.on('#credits-btn', 'click', () => {
            window.location.href = config.routes.credit;
        });
        
        DomUtils.on('#back-to-accounts-btn', 'click', () => {
            this.accountDetailsComponent.backToAccountList();
        });

        DomUtils.on('#close-account-modal-btn', 'click', () => {
            DomUtils.hideModal('account-modal');
        });

        DomUtils.on('#account-modal-submit', 'click', async () => {
            const amount = DomUtils.find('#amount').value;
            const accountId = DomUtils.find('#account-modal-submit').dataset.accountId;
            const action = DomUtils.find('#account-modal-submit').dataset.action;
            
            DomUtils.hideModal('account-modal');
            
            if (action === 'deposit') {
                await this.accountsComponent.processDeposit(amount, accountId);
            } else if (action === 'withdraw') {
                await this.accountsComponent.processWithdraw(amount, accountId);
            }
        });

        DomUtils.on('#account-type-filter', 'change', (e) => {
            this.accountsComponent.setFilters(e.target.value, this.accountsComponent.filters.status);
        });

        DomUtils.on('#account-status-filter', 'change', (e) => {
            this.accountsComponent.setFilters(this.accountsComponent.filters.type, e.target.value);
        });

        DomUtils.on('#apply-filters-btn', 'click', () => {
            this.accountsComponent.renderAccounts();
        });

        DomUtils.on('#reset-filters-btn', 'click', () => {
            this.accountsComponent.setFilters('all', 'all');
            
            const typeFilter = DomUtils.find('#account-type-filter');
            const statusFilter = DomUtils.find('#account-status-filter');
            
            if (typeFilter) typeFilter.value = 'all';
            if (statusFilter) statusFilter.value = 'all';
            
            this.accountsComponent.renderAccounts();
        });

        DomUtils.on('#theme-toggle', 'click', () => {
            this.themeComponent.toggleTheme();
        });

        DomUtils.on('#accounts-list', 'click', (e) => {
            if (e.target.classList.contains('account-visibility-toggle') || 
                e.target.closest('.account-visibility-toggle')) {
                const button = e.target.classList.contains('account-visibility-toggle') 
                    ? e.target 
                    : e.target.closest('.account-visibility-toggle');
                const accountId = button.dataset.id;
                this.accountsComponent.toggleAccountVisibility(accountId);
            }
        });
    }

    async loadAccounts() {
        return this.accountsComponent.loadAccounts();
    }
    
    async reloadAccounts() {
        return this.accountsComponent.loadAccounts();
    }
    
    loadAccountDetails(accountId) {
        this.accountDetailsComponent.loadAccountDetails(accountId);
    }

    getUserData() {
        return authService.getCurrentUser();
    }
}
