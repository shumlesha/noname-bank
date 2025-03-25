const API_CONFIG = {
    BASE_URL: '/api',
    AUTH_ENDPOINT: '/api/auth',
    USERS_ENDPOINT: '/api/users',
    QUERY_ENDPOINT: '/api/query',
    CREDIT_ENDPOINT: '/api/credit',
    WS_BASE_URL: window.location.protocol === 'https:' ? 'wss://' : 'ws://' + window.location.host,

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
        creditsByClient: '/api/credit/query',
        creditTariffsAll: '/api/credit/tariff/query/all',
        creditTariffById: '/api/credit/tariff/query',
        creditTariffCreate: '/api/credit/tariff/command/create',
        creditTariffUpdate: '/api/credit/tariff/command/update',
        creditTariffDelete: '/api/credit/tariff/command/delete',
        wsTransactionEmployee: '/ws/employee/transaction'
    },

    PAGINATION: {
        ACCOUNTS_PER_PAGE: 10,
        USERS_PER_PAGE: 10
    }
};

export default API_CONFIG;