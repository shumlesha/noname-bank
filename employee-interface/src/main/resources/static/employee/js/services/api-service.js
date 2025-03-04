import API_CONFIG from '../config/api-config.js';
import storageService from './storage-service.js';

const apiService = {
    fetch: async (url, options = {}) => {
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

        if (fetchOptions.skipAuth) delete fetchOptions.skipAuth;

        try {
            const response = await fetch(url, fetchOptions);

            let data;
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                const text = await response.text();
                data = { message: text };
            }

            if (!response.ok) {
                console.error('Ошибка API:', data);
                throw new Error(data.message || `Ошибка сервера: ${response.status}`);
            }

            return data;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    },

    register: async (userData) => {
        const options = {
            method: 'POST',
            body: JSON.stringify({
                email: userData.email,
                fullName: userData.fullName,
                gender: userData.gender,
                roles: userData.roles,
                password: userData.password
            }),
            skipAuth: true
        };

        return apiService.fetch(API_CONFIG.ENDPOINTS.register, options);
    },


    login: async (credentials) => {
        const options = {
            method: 'POST',
            body: JSON.stringify(credentials),
            skipAuth: true
        };

        return apiService.fetch(API_CONFIG.ENDPOINTS.login, options);
    },


    logout: async () => {
        const options = { method: 'POST' };
        return apiService.fetch(API_CONFIG.ENDPOINTS.logout, options);
    },


    getUserMe: async () => {
        const options = { method: 'GET' };
        try {
            return await apiService.fetch(API_CONFIG.ENDPOINTS.me, options);
        } catch (error) {
            console.error('Error in getUserMe:', error);
            throw error;
        }
    },


    getAllUsers: async (pageSize = 10, pageOffset = 0) => {
        const options = {
            method: 'GET'
        };
        const pageable = {
            size: pageSize,
            page: pageOffset,
            sort: 'fullName,asc'
        };
        const params = new URLSearchParams();
        for (const [key, value] of Object.entries(pageable)) {
            params.append(key, value);
        }
        
        return apiService.fetch(`${API_CONFIG.ENDPOINTS.usersAll}?${params.toString()}`, options);
    },


    getUserById: async (userId) => {
        const options = {
            method: 'GET'
        };
        
        return apiService.fetch(`${API_CONFIG.ENDPOINTS.userById}/${userId}`, options);
    },


    banUser: async (userId, reason) => {
        const options = {
            method: 'POST',
            body: JSON.stringify({ reason: reason || "" })
        };
        
        return apiService.fetch(`${API_CONFIG.ENDPOINTS.userBan}/${userId}`, options);
    },


    getAllAccounts: async (pageSize = 10, pageOffset = 0) => {
        return apiService.fetch(API_CONFIG.ENDPOINTS.accountAll, {
            method: 'POST',
            body: JSON.stringify({ size: pageSize, offset: pageOffset })
        });
    },


    getAccountDetails: async (accountId, clientId) => {
        return apiService.fetch(API_CONFIG.ENDPOINTS.accountDetails, {
            method: 'POST',
            body: JSON.stringify({ accountId, clientId })
        });
    },


    getClientAccounts: async (clientId) => {
        return apiService.fetch(API_CONFIG.ENDPOINTS.accountList, {
            method: 'POST',
            body: JSON.stringify({ clientId })
        });
    },


    getAccountTransactions: async (accountId) => {
        return apiService.fetch(API_CONFIG.ENDPOINTS.transactionAccount, {
            method: 'POST',
            body: JSON.stringify({ accountId })
        });
    },


    getClientTransactions: async (clientId) => {
        return apiService.fetch(API_CONFIG.ENDPOINTS.transactionClient, {
            method: 'POST',
            body: JSON.stringify({ clientId })
        });
    },


    getTransactionDetails: async (transactionId, clientId) => {
        return apiService.fetch(API_CONFIG.ENDPOINTS.transaction, {
            method: 'POST',
            body: JSON.stringify({ transactionId, clientId })
        });
    },


    getClientCredits: async (clientId) => {
        return apiService.fetch(`${API_CONFIG.ENDPOINTS.creditsByClient}/${clientId}`, {
            method: 'GET'
        });
    }
};

export default apiService;