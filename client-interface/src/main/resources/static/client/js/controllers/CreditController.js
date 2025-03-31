import {authService} from '../core/authService.js';
import {DomUtils} from '../utils/domUtils.js';
import {config} from '../config/config.js';
import {settingsService} from '../core/settingsService.js';
import {ThemeComponent} from '../components/ThemeComponent.js';
import {CreditRatingComponent} from '../components/CreditRatingComponent.js';
import {MissedPaymentsComponent} from '../components/MissedPaymentsComponent.js';
import {CreditsComponent} from '../components/CreditsComponent.js';
import {CreditTariffsComponent} from '../components/CreditTariffsComponent.js';

export class CreditController {
    constructor() {
        settingsService.loadCachedSettings();

        this.themeComponent = new ThemeComponent(this);
        this.creditRatingComponent = new CreditRatingComponent(this);
        this.missedPaymentsComponent = new MissedPaymentsComponent(this);
        this.creditsComponent = new CreditsComponent(this);
        this.creditTariffsComponent = new CreditTariffsComponent(this);
        
        this.themeComponent.applyCurrentTheme();
        
        this.init();
    }

    init() {
        authService.loadUserData().then(async () => {
            const userData = this.getUserData();
            this.displayUserData(userData);

            const settingsPromise = this.loadUserSettings(userData.userId);
            const ratingPromise = this.loadCreditRating(userData.userId);
            const creditsPromise = this.loadCredits(userData.userId);
            const missedPaymentsPromise = this.loadMissedPayments(userData.userId);

            await Promise.all([settingsPromise, ratingPromise, creditsPromise, missedPaymentsPromise]);

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
        if (userEmailEl) userEmailEl.textContent = userData.email;
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
        
        DomUtils.on('#home-btn', 'click', () => {
            window.location.href = config.routes.home;
        });
        
        DomUtils.on('#fetch-tariffs-btn', 'click', async () => {
            await this.fetchCreditTariffs();
        });
        
        DomUtils.on('#close-modal-btn', 'click', () => {
            DomUtils.hideModal('tariff-modal');
        });

        DomUtils.on('#theme-toggle', 'click', () => {
            this.themeComponent.toggleTheme();
        });
    }

    async loadCreditRating(userId) {
        return this.creditRatingComponent.loadCreditRating(userId);
    }
    
    async loadMissedPayments(userId) {
        return this.missedPaymentsComponent.loadMissedPayments(userId);
    }
    
    async loadCredits(userId) {
        return this.creditsComponent.loadCredits(userId);
    }
    
    async reloadCredits() {
        const userData = this.getUserData();
        return this.loadCredits(userData.userId);
    }
    
    async fetchCreditTariffs() {
        return this.creditTariffsComponent.fetchCreditTariffs();
    }

    getUserData() {
        return authService.getCurrentUser();
    }
}
