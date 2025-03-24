import {storageService} from '../core/storageService.js';
import {config} from '../config/config.js';

export class HttpClient {
    static async fetch(url, options = {}, skipAuth = false) {
        const tokenData = storageService.getTokens();

        const defaultOptions = {
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        };

        // if (tokenData?.accessToken && !skipAuth) {
        //     defaultOptions.headers['Authorization'] = `Bearer ${tokenData.accessToken}`;
        // }

        try {
            const response = await fetch(url, { ...defaultOptions, ...options });
            const text = await response.text();
            const data = text ? JSON.parse(text) : null;

            return data;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    }
}
