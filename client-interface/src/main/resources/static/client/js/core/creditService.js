import {config} from "../config/config.js";
import {Credit} from "../models/Credit.js";
import {CreditTariff} from "../models/CreditTariff.js";
import {apiService} from "./apiService.js";

class CreditService {
    async loadCredits(clientId) {
        const response = await apiService.get(`${config.api.endpoints.credit.list}/${clientId}`);
        return {
            ...response,
            data: response.data.map(creditData => new Credit(creditData))
        };
    }

    async payCredit(creditId, amount) {
        return apiService.post(config.api.endpoints.credit.pay, {
            creditId,
            amount: parseFloat(amount)
        });
    }

    async fetchCreditTariffs() {
        const response = await apiService.get(config.api.endpoints.credit.tariffs);
        return {
            ...response,
            data: response.data.map(tariffData => new CreditTariff(tariffData))
        };
    }

    async takeCredit(clientId, tariffId, amount) {
        return apiService.post(config.api.endpoints.credit.create, {
            clientId,
            amount: parseFloat(amount),
            tariffId
        });
    }

    async getCreditRating(clientId) {
        return apiService.get(`${config.api.endpoints.credit.rating}/${clientId}`);
    }

    async getMissedPayments(clientId) {
        return apiService.get(`${config.api.endpoints.credit.missed}/${clientId}`);
    }
}

export const creditService = new CreditService();
