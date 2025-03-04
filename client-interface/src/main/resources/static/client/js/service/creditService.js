import { apiService } from './apiService.js';

export const creditService = {
    async loadCredits(clientId) {
        return apiService.fetch(`/api/credit/query/${clientId}`);
    },

    async payCredit(creditId, amount) {
        return apiService.fetch('/api/credit/command/pay', {
            method: 'POST',
            body: JSON.stringify({ creditId, amount })
        });
    }
};
