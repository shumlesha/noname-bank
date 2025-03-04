import { BaseModel } from './BaseModel.js';

export class CreditTariff extends BaseModel {
    constructor(data = {}) {
        super(data);
        this.id = data.id || '';
        this.name = data.name || '';
        this.interestRate = data.interestRate || 0;
    }

    getFormattedInterestRate() {
        return `${this.interestRate}%`;
    }
}
