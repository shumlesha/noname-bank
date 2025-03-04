import { BaseModel } from './BaseModel.js';

export class Account extends BaseModel {

    constructor(data = {}) {
        super(data);
        this.id = data.id || '';
        this.number = data.number || '';
        this.balance = data.balance || 0;
        this.isCredit = data.isCredit || false;
        this.closedTimestamp = data.closedTimestamp || null;
        this.clientId = data.clientId || '';
    }

    isActive() {
        return !this.closedTimestamp;
    }

    getType() {
        return this.isCredit ? 'Кредитный' : 'Дебетовый';
    }

    getStatus() {
        return this.isActive() ? 'Активен' : 'Закрыт';
    }

    getFormattedBalance() {
        return `${this.balance} ₽`;
    }
}
