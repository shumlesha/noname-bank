import {config} from '../config/config.js';
import {settingsService} from '../core/settingsService.js';

export class ThemeComponent {
    constructor(parentController) {
        this.parentController = parentController;
    }

    applyCurrentTheme() {
        const currentTheme = settingsService.getCurrentTheme();
        if (currentTheme === config.themes.DARK) {
            document.documentElement.classList.add('dark-theme');
            document.body.classList.add('dark-theme');
        } else {
            document.documentElement.classList.remove('dark-theme');
            document.body.classList.remove('dark-theme');
        }
    }
    
    toggleTheme() {
        const userData = this.parentController.getUserData();
        if (!userData) return;

        const currentTheme = settingsService.getCurrentTheme();
        const newTheme = currentTheme === config.themes.LIGHT ? config.themes.DARK : config.themes.LIGHT;

        settingsService.updateThemeLocally(newTheme);
        this.applyCurrentTheme();

        settingsService.syncThemeWithServer(userData.userId, newTheme)
            .catch(error => {
                console.error("Ошибка при синхронизации темы с сервером:", error);
            });
    }
} 