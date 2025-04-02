export const config = {
    api: {
        baseUrl: '',
        endpoints: {
            auth: {
                login: '/api/auth/login',
                register: '/api/auth/register',
                logout: '/api/auth/logout'
            },
            account: {
                list: '/api/query/account/list',
                create: '/api/account/create',
                close: '/api/account/close',
                details: '/api/query/account'
            },
            transaction: {
                list: '/api/query/transaction/account',
                create: '/api/transaction/create'
            },
            atm: {
                deposit: '/api/atm/deposit',
                withdraw: '/api/atm/withdraw'
            },
            credit: {
                list: '/api/credit/query',
                pay: '/api/credit/command/pay',
                tariffs: '/api/credit/tariff/query/all',
                create: '/api/credit/command',
                rating: '/api/credit/rating/query',
                missed: '/api/credit/missed/query'
            },
            user: {
                me: '/api/users/me'
            },
            settings: {
                get: '/api/settings',
                updateTheme: '/api/settings/theme',
                hideAccount: '/api/settings/hide-account',
                unhideAccount: '/api/settings/unhide-account'
            }
        }
    },

    websocket: {
        transaction: '/ws/employee/transaction'
    },

    storage: {
        tokens: 'auth_tokens',
        userData: 'user_data',
        userSettings: 'user_settings'
    },

    routes: {
        home: '/client/home',
        login: '/client/login',
        register: '/client/register',
        credit: '/client/credit'
    },
    
    themes: {
        LIGHT: 'LIGHT',
        DARK: 'DARK'
    }
};
