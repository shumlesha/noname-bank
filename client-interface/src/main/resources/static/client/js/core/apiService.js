import {HttpClient} from '../utils/httpClient.js';

class ApiService {
    async request(endpoint, options = {}, skipAuth = false, additionalOptions = {}) {
        try {
            const response = await HttpClient.fetch(endpoint, {
                ...options,
                ...additionalOptions
            }, skipAuth);
            return response;
        } catch (error) {
            if (error instanceof SyntaxError) {
                const textResponse = await HttpClient.fetch(endpoint, {
                    ...options,
                    ...additionalOptions,
                    responseType: 'text'
                }, skipAuth);
                throw new Error(textResponse);
            }
            throw error;
        }
    }

    async post(endpoint, data, skipAuth = false, additionalOptions = {}) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        }, skipAuth, additionalOptions);
    }

    async get(endpoint, skipAuth = false, additionalOptions = {}) {
        return this.request(endpoint, {
            method: 'GET'
        }, skipAuth, additionalOptions);
    }
}

export const apiService = new ApiService();
