import {HttpClient} from '../utils/httpClient.js';

class ApiService {
    async request(endpoint, options = {}, skipAuth = false) {
        return HttpClient.fetch(endpoint, options, skipAuth);
    }

    async post(endpoint, data, skipAuth = false) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        }, skipAuth);
    }

    async get(endpoint, skipAuth = false) {
        return this.request(endpoint, {
            method: 'GET'
        }, skipAuth);
    }
}

export const apiService = new ApiService();
