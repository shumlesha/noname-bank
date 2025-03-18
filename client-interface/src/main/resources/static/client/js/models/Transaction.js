import {BaseModel} from './BaseModel.js';

export class Transaction extends BaseModel {

    constructor(data = {}) {
        super(data);
        this.id = data.id || '';
        this.amount = data.amount || 0;
        this.accountFrom = data.accountFrom || '';
        this.accountTo = data.accountTo || '';
        this.transactionTimestamp = data.transactionTimestamp || '';
    }

    getType(accountId) {
        return this.accountFrom === accountId ? 'Списание' : 'Пополнение';
    }

    getFormattedAmount(accountId) {
        const prefix = this.accountFrom === accountId ? '-' : '+';
        return `${prefix} ${this.amount} ₽`;
    }
}
