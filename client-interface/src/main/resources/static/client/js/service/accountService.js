import { apiService } from '../api/apiService.js';

export const accountService = {
    async loadAccounts(userId) {
        return apiService.fetch('/api/query/account/list', {
            method: 'POST',
            body: JSON.stringify({ clientId: userId })
        });
    },

    async createAccount(userId) {
        return apiService.fetch('/api/account/create', {
            method: 'POST',
            body: JSON.stringify({ clientId: userId })
        });
    },

    async closeAccount(userId, accountId) {
        return apiService.fetch('/api/account/close', {
            method: 'POST',
            body: JSON.stringify({ clientId: userId, accountId })
        });
    },

    async depositMoney(amount, accountId) {
        return apiService.fetch('/api/atm/deposit', {
            method: 'POST',
            body: JSON.stringify({ amount, accountId })
        });
    },

    async withdrawMoney(amount, accountId){
        return apiService.fetch('/api/atm/withdraw', {
            method: 'POST',
            body: JSON.stringify({ amount, accountId })
        });
    },

    async loadAccountDetails(userId, accountId) {
        const accountResponse = await apiService.fetch('/api/query/account', {
            method: 'POST',
            body: JSON.stringify({ clientId: userId, accountId })
        });

        const transactionsResponse = await apiService.fetch('/api/query/transaction/account', {
            method: 'POST',
            body: JSON.stringify({ accountId })
        });

        return {
            account: accountResponse,
            transactions: transactionsResponse.transactions
        };
    }
};
