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
                list: '/api/query/transaction/account'
            },
            atm: {
                deposit: '/api/atm/deposit',
                withdraw: '/api/atm/withdraw'
            },
            credit: {
                list: '/api/credit/query',
                pay: '/api/credit/command/pay',
                tariffs: '/api/credit/tariff/query/all',
                create: '/api/credit/command'
            }
        }
    },

    storage: {
        tokens: 'auth_tokens',
        userData: 'user_data'
    },

    routes: {
        home: '/client/home',
        login: '/client/login',
        register: '/client/register',
        credit: '/client/credit'
    }
};
