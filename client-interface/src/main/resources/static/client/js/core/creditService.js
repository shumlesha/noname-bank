import { config } from "../config/config.js";
import { Credit } from "../models/Credit.js";
import { CreditTariff } from "../models/CreditTariff.js";

class CreditService {
    async loadCredits(clientId) {
        const response = await fetch(`${config.api.endpoints.credit.list}/${clientId}`);
        const data = await response.json();
        return {
            ...data,
            data: data.data.map(creditData => new Credit(creditData))
        };
    }

    async payCredit(creditId, amount) {
        return fetch(config.api.endpoints.credit.pay, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ creditId, amount: parseFloat(amount) })
        }).then(res => res.json());
    }

    async fetchCreditTariffs() {
        const response = await fetch(config.api.endpoints.credit.tariffs);
        const data = await response.json();
        return {
            ...data,
            data: data.data.map(tariffData => new CreditTariff(tariffData))
        };
    }

    async takeCredit(clientId, tariffId, amount) {
        return fetch(config.api.endpoints.credit.create, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                clientId,
                amount: parseFloat(amount),
                tariffId
            })
        }).then(res => res.json());
    }
}

export const creditService = new CreditService();