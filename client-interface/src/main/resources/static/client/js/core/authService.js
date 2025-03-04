import { apiService } from './apiService.js';
import { storageService } from './storageService.js';
import { config } from '../config/config.js';
import { User } from '../models/User.js';

class AuthService {
    async login(credentials) {
        const response = await apiService.post(
            config.api.endpoints.auth.login, 
            credentials, 
            true
        );

        storageService.saveTokens(response.data);
        storageService.saveUserData({
            userId: response.data.userId,
            email: credentials.email
        });

        return response.data;
    }

    async register(userData) {
        const response = await apiService.post(
            config.api.endpoints.auth.register, 
            userData, 
            true
        );
        
        return response.data;
    }

    async logout() {
        const tokenData = storageService.getTokens();
        if (!tokenData?.refreshToken) {
            throw new Error('Токен обновления не найден');
        }

        await apiService.post(
            config.api.endpoints.auth.logout, 
            { refreshToken: tokenData.refreshToken }
        );

        storageService.removeTokens();
        storageService.removeUserData();
    }
    

    isAuthenticated() {
        const tokenData = storageService.getTokens();
        const userData = storageService.getUserData();
        return !!(tokenData?.accessToken && userData?.userId);
    }

    getCurrentUser() {
        const userData = storageService.getUserData();
        return userData ? new User(userData) : null;
    }
}

export const authService = new AuthService();
