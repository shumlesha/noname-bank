import { storageService } from '../core/storageService.js';
import { config } from '../config/config.js';

export class HttpClient {
    static async fetch(url, options = {}, skipAuth = false) {
        const tokenData = storageService.getTokens();

        const defaultOptions = {
            headers: { 'Content-Type': 'application/json' }
        };

        if (tokenData?.accessToken && !skipAuth) {
            defaultOptions.headers['Authorization'] = `Bearer ${tokenData.accessToken}`;
        }

        try {
            const response = await fetch(url, { ...defaultOptions, ...options });
            const text = await response.text();
            const data = text ? JSON.parse(text) : null;

            if (!response.ok) {
                if (response.status === 401) {
                    console.log('Не авторизован. Редирект на страницу логина');
                    storageService.removeTokens();
                    storageService.removeUserData();
                    window.location.href = config.routes.login;
                }
                throw new Error(data?.message || `Ошибка ${response.status}`);
            }

            return data;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    }
}
