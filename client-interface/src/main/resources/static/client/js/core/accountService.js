import {apiService} from './apiService.js';
import {config} from '../config/config.js';
import {Account} from '../models/Account.js';
import {Transaction} from '../models/Transaction.js';

class AccountService {
    async loadAccounts(userId) {
        const response = await apiService.post(
            config.api.endpoints.account.list, 
            { clientId: userId }
        );
        
        return {
            ...response,
            data: response.data.map(accountData => new Account(accountData))
        };
    }

    async createAccount(userId, currency) {
        const response = await apiService.post(
            config.api.endpoints.account.create, 
            { 
                clientId: userId,
                currency: currency 
            }
        );
        
        return new Account(response);
    }

    async closeAccount(userId, accountId) {
        return apiService.post(
            config.api.endpoints.account.close, 
            { clientId: userId, accountId }
        );
    }

    async depositMoney(amount, accountId) {
        return apiService.post(
            config.api.endpoints.atm.deposit, 
            { amount: parseFloat(amount), accountId }
        );
    }

    async withdrawMoney(amount, accountId) {
        return apiService.post(
            config.api.endpoints.atm.withdraw, 
            { amount: parseFloat(amount), accountId }
        );
    }

    async loadAccountDetails(userId, accountId) {
        const accountResponse = await apiService.post(
            config.api.endpoints.account.details, 
            { clientId: userId, accountId }
        );

        const transactionsResponse = await apiService.post(
            config.api.endpoints.transaction.list, 
            { accountId }
        );

        return {
            account: new Account(accountResponse),
            transactions: transactionsResponse.transactions.map(
                txData => new Transaction(txData)
            )
        };
    }
}

export const accountService = new AccountService();
