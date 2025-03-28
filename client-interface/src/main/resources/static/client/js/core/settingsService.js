import {apiService} from './apiService.js';
import {config} from '../config/config.js';

class SettingsService {
    constructor() {
        this.settings = null;
    }

    async loadUserSettings(userId) {
        try {
            const cachedSettings = this.getCachedSettings();
            if (cachedSettings) {
                this.settings = cachedSettings;
                return cachedSettings;
            }

            const response = await apiService.get(`/api/settings/${userId}`);
            if (response && response.data) {
                this.settings = response.data;
                this.cacheSettings(response.data);
                return response.data;
            }
            return null;
        } catch (error) {
            console.error('Ошибка при загрузке настроек:', error);
            const defaultSettings = {
                userId,
                theme: 'LIGHT',
                hiddenAccounts: []
            };
            this.settings = defaultSettings;
            this.cacheSettings(defaultSettings);
            return defaultSettings;
        }
    }
    
    updateThemeLocally(theme) {
        if (!this.settings) {
            this.settings = { 
                theme: theme, 
                hiddenAccounts: [] 
            };
        } else {
            this.settings.theme = theme;
        }
        this.cacheSettings(this.settings);
        console.log(`Тема успешно обновлена локально на: ${theme}`);
    }

    hideAccountLocally(accountId) {
        if (!this.settings) {
            this.settings = { 
                theme: 'LIGHT', 
                hiddenAccounts: [accountId.toString()] 
            };
        } else {
            if (!this.settings.hiddenAccounts) {
                this.settings.hiddenAccounts = [];
            }
            const accountIdStr = accountId.toString();
            if (!this.settings.hiddenAccounts.some(id => id.toString() === accountIdStr)) {
                this.settings.hiddenAccounts.push(accountIdStr);
            }
        }
        this.cacheSettings(this.settings);
        console.log(`Счет ${accountId} успешно скрыт локально`);
    }

    unhideAccountLocally(accountId) {
        if (this.settings && this.settings.hiddenAccounts) {
            const accountIdStr = accountId.toString();
            this.settings.hiddenAccounts = this.settings.hiddenAccounts.filter(
                id => id.toString() !== accountIdStr
            );
            this.cacheSettings(this.settings);
            console.log(`Счет ${accountId} успешно показан локально`);
        }
    }
    
    async syncThemeWithServer(userId, theme) {
        try {
            console.log(`Синхронизация темы с сервером: ${theme}`);
            const response = await apiService.post('/api/settings/theme', {
                clientId: userId,
                theme: theme
            });
            
            if (response && response.data) {
                console.log('Получены обновленные настройки с сервера:', response.data);
                if (this.settings) {
                    this.settings.hiddenAccounts = response.data.hiddenAccounts || this.settings.hiddenAccounts;
                } else {
                    this.settings = response.data;
                }
                this.cacheSettings(this.settings);
            }
            return this.settings;
        } catch (error) {
            console.error('Ошибка при синхронизации темы с сервером:', error);
            throw error;
        }
    }

    async syncHideAccountWithServer(userId, accountId) {
        try {
            console.log(`Синхронизация скрытия счета ${accountId} с сервером`);
            const response = await apiService.post('/api/settings/hide-account', {
                clientId: userId,
                accountId: accountId
            });
            
            if (response && response.data) {
                console.log('Получены обновленные настройки с сервера:', response.data);

                const localTheme = this.settings ? this.settings.theme : 'LIGHT';

                this.settings = response.data;

                if (this.settings.theme !== localTheme) {
                    this.settings.theme = localTheme;
                }
                
                this.cacheSettings(this.settings);
            }
            return this.settings;
        } catch (error) {
            console.error('Ошибка при синхронизации скрытия счета с сервером:', error);
            throw error;
        }
    }

    async syncUnhideAccountWithServer(userId, accountId) {
        try {
            console.log(`Синхронизация отображения счета ${accountId} с сервером`);
            const response = await apiService.post('/api/settings/unhide-account', {
                clientId: userId,
                accountId: accountId
            });
            
            if (response && response.data) {
                console.log('Получены обновленные настройки с сервера:', response.data);

                const localTheme = this.settings ? this.settings.theme : 'LIGHT';

                this.settings = response.data;

                if (this.settings.theme !== localTheme) {
                    this.settings.theme = localTheme;
                }
                
                this.cacheSettings(this.settings);
            }
            return this.settings;
        } catch (error) {
            console.error('Ошибка при синхронизации отображения счета с сервером:', error);
            throw error;
        }
    }

    isAccountHidden(accountId) {
        if (!this.settings || !this.settings.hiddenAccounts) {
            return false;
        }

        const accountIdStr = accountId.toString();
        console.log('Проверка скрытия счета:', accountIdStr);
        console.log('Список скрытых счетов:', this.settings.hiddenAccounts);

        return this.settings.hiddenAccounts.some(id => id.toString() === accountIdStr);
    }

    getCurrentTheme() {
        if (!this.settings) {
            return 'LIGHT';
        }
        return this.settings.theme || 'LIGHT';
    }

    cacheSettings(settings) {
        localStorage.setItem(config.storage.userSettings, JSON.stringify(settings));
    }

    getCachedSettings() {
        const settingsStr = localStorage.getItem(config.storage.userSettings);
        return settingsStr ? JSON.parse(settingsStr) : null;
    }

    clearCachedSettings() {
        localStorage.removeItem(config.storage.userSettings);
        this.settings = null;
    }

    loadCachedSettings() {
        const settings = this.getCachedSettings();
        if (settings) {
            this.settings = settings;
            console.log('Настройки успешно загружены из localStorage:', settings);
            return true;
        }
        console.log('Настройки не найдены в localStorage');
        return false;
    }
}

export const settingsService = new SettingsService(); 