import { apiService } from '../api/apiService.js';
import { storageService } from '../storage/storageService.js';

export const authService = {
    async login(credentials) {
        const response = await apiService.fetch('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials),
            skipAuth: true
        });

        storageService.saveTokens(response.data);
        storageService.saveUserData({
            userId: response.data.userId,
            email: credentials.email
        });

        return response.data;
    },

    async register(userData) {
        const response = await apiService.fetch('/api/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData),
            skipAuth: true
        });
        return response.data;
    },

    async logout() {
        const tokenData = storageService.getTokens();
        if (!tokenData?.refreshToken) throw new Error('Токен обновления не найден');

        await apiService.fetch('/api/auth/logout', {
            method: 'POST',
            body: JSON.stringify({ refreshToken: tokenData.refreshToken })
        });

        storageService.removeTokens();
        storageService.removeUserData();
    }
};
