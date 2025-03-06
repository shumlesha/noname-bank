import { BaseModel } from './BaseModel.js';

export class CreditTariff extends BaseModel {
    constructor(data = {}) {
        super(data);
        this.id = data.id || '';
        this.name = data.name || '';
        this.interestRate = data.interestRate || 0;
        this.autoPaymentRate = data.autoPaymentRate || 0;
        this.penaltyRate = data.penaltyRate || 0;
    }

    getFormattedInterestRate() {
        return `${this.interestRate}%`;
    }

    getFormattedAutoPaymentRate() {
        return `${this.autoPaymentRate}%`;
    }

    getFormattedPenaltyRate() {
        return `${this.penaltyRate}%`;
    }
}
