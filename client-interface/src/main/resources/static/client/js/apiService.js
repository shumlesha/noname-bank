const API_BASE_URL = '/api';
const AUTH_ENDPOINT = `${API_BASE_URL}/auth`;
const TOKEN_STORAGE_KEY = 'auth_tokens';
const USER_STORAGE_KEY = 'user_data';

const API_URLS = {
    register: `${AUTH_ENDPOINT}/register`,
    login: `${AUTH_ENDPOINT}/login`,
    logout: `${AUTH_ENDPOINT}/logout`
};

const apiService = {
    fetch: async function(url, options = {}) {
        const tokenData = storageService.getTokens();

        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json'
            }
        };

        if (tokenData && tokenData.accessToken && !options.skipAuth) {
            defaultOptions.headers['Authorization'] = `Bearer ${tokenData.accessToken}`;
        }

        const fetchOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...(options.headers || {})
            }
        };

        try {
            const response = await fetch(url, fetchOptions);

            const text = await response.text();
            const data = text.length > 0 ? JSON.parse(text) : null;

            if (!response.ok) {
                throw new Error(data?.message || `Ошибка ${response.status}: ${response.statusText}`);
            }

            return data;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    },

    register: async function(userData) {
        const options = {
            method: 'POST',
            body: JSON.stringify(userData),
            skipAuth: true
        };

        return this.fetch(API_URLS.register, options);
    },

    login: async function(credentials) {
        const options = {
            method: 'POST',
            body: JSON.stringify(credentials),
            skipAuth: true
        };

        return this.fetch(API_URLS.login, options);
    },

    logout: async function() {
        const tokenData = storageService.getTokens();

        if (!tokenData || !tokenData.refreshToken) {
            throw new Error('Токен обновления не найден');
        }

        const options = {
            method: 'POST',
            body: JSON.stringify({
                refreshToken: tokenData.refreshToken
            })
        };

        return this.fetch(API_URLS.logout, options);
    }
};
