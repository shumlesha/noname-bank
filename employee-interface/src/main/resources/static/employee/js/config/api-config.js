const API_CONFIG = {
    BASE_URL: '/api',
    AUTH_ENDPOINT: '/api/auth',
    USERS_ENDPOINT: '/api/users',
    QUERY_ENDPOINT: '/api/query',
    CREDIT_ENDPOINT: '/api/credit',

    STORAGE_KEYS: {
        TOKEN: 'auth_tokens',
        USER: 'user_data'
    },

    ENDPOINTS: {
        register: '/api/auth/register',
        login: '/api/auth/login',
        logout: '/api/auth/logout',
        me: '/api/users/me',
        usersAll: '/api/users/all',
        userBan: '/api/users/ban',
        userById: '/api/users',
        accountAll: '/api/query/account/all',
        accountDetails: '/api/query/account',
        accountList: '/api/query/account/list',
        transactionClient: '/api/query/transaction/client',
        transactionAccount: '/api/query/transaction/account',
        transaction: '/api/query/transaction',
        creditsByClient: '/api/credit/query'
    },

    PAGINATION: {
        ACCOUNTS_PER_PAGE: 10,
        USERS_PER_PAGE: 10
    }
};

export default API_CONFIG;