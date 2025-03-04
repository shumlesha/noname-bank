import { apiService } from '../api/apiService.js';

export const creditService = {
    async loadCredits(clientId) {
        return apiService.fetch(`/api/credit/query/${clientId}`);
    },

    async payCredit(creditId, amount) {
        return apiService.fetch('/api/credit/command/pay', {
            method: 'POST',
            body: JSON.stringify({ creditId, amount: parseFloat(amount) })
        });
    },

    async fetchCreditTariffs() {
        return apiService.fetch('/api/credit/tariff/query/all');
    },

    async takeCredit(clientId, tariffId, amount) {
        return apiService.fetch('/api/credit/command', {
            method: 'POST',
            body: JSON.stringify({ clientId, amount: parseFloat(amount), tariffId })
        });
    }
};
