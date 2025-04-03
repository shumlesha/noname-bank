import API_CONFIG from '../config/api-config.js';
import apiService from './api-service.js';
import storageService from './storage-service.js';

const THEME_KEY = 'user_theme';
const DEFAULT_THEME = 'light';

const themeService = {
    init: async () => {
        const localTheme = localStorage.getItem(THEME_KEY);
        if (localTheme) {
            themeService.applyTheme(localTheme);
        } else {
            themeService.applyTheme(DEFAULT_THEME);
        }

        try {
            const settings = await themeService.fetchThemeFromServer();
            if (settings && settings.theme) {
                const serverTheme = settings.theme.toLowerCase();
                if (localTheme !== serverTheme) {
                    themeService.applyTheme(serverTheme);
                    localStorage.setItem(THEME_KEY, serverTheme);
                }
            }
        } catch (error) {
            console.error('Error fetching theme from server:', error);
        }
    },


    fetchThemeFromServer: async () => {
        try {
            const response = await apiService.fetch('/api/settings', {
                method: 'GET'
            });
            
            if (response && response.data) {
                return response.data;
            }
            return null;
        } catch (error) {
            console.error('Failed to fetch theme settings:', error);
            throw error;
        }
    },


    updateThemeOnServer: async (theme) => {
        try {
            const response = await apiService.fetch('/api/settings/theme', {
                method: 'POST',
                body: JSON.stringify({
                    theme: theme.toUpperCase()
                })
            });
            
            return response && response.data;
        } catch (error) {
            console.error('Failed to update theme on server:', error);
            return null;
        }
    },


    toggleTheme: async () => {
        const currentTheme = localStorage.getItem(THEME_KEY) || DEFAULT_THEME;
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';

        themeService.applyTheme(newTheme);
        localStorage.setItem(THEME_KEY, newTheme);

        try {
            await themeService.updateThemeOnServer(newTheme);
        } catch (error) {
            console.error('Error updating theme on server:', error);
        }
    },


    applyTheme: (theme) => {
        document.documentElement.setAttribute('data-theme', theme);

        const themeToggleIcon = document.getElementById('theme-toggle-icon');
        if (themeToggleIcon) {
            themeToggleIcon.className = theme === 'dark' 
                ? 'fas fa-sun' 
                : 'fas fa-moon';
            
            const themeToggleText = document.getElementById('theme-toggle-text');
            if (themeToggleText) {
                themeToggleText.textContent = theme === 'dark' 
                    ? 'Светлая тема' 
                    : 'Темная тема';
            }
        }
    },


    getCurrentTheme: () => {
        return localStorage.getItem(THEME_KEY) || DEFAULT_THEME;
    }
};

export default themeService;
