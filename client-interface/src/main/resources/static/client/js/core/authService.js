import {apiService} from './apiService.js';
import {storageService} from './storageService.js';
import {config} from '../config/config.js';
import {User} from '../models/User.js';
import {settingsService} from './settingsService.js';

class AuthService {

    logout() {
        window.location.href = "/";
        storageService.removeUserData();
        settingsService.clearCachedSettings();
    }

    async loadUserData() {
        try {
            const response = await apiService.get(config.api.endpoints.user.me);
            if (response && response.data) {
                storageService.saveUserData({
                    userId: response.data.id,
                    email: response.data.email
                });
                return response.data;
            }
        } catch (error) {
            console.error("Ошибка при загрузке данных пользователя:", error);
        }
        return null;
    }

    getCurrentUser() {
        const userData = storageService.getUserData();
        return userData ? new User(userData) : null;
    }
}

export const authService = new AuthService();
