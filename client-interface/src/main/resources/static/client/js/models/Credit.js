import { BaseModel } from './BaseModel.js';

export class Credit extends BaseModel {
    constructor(data = {}) {
        super(data);
        this.id = data.id || '';
        this.amount = data.amount || 0;
        this.paidAmount = data.paidAmount || 0;
        this.nextPaymentDate = data.nextPaymentDate || '';
        this.tariffId = data.tariffId || '';
        this.tariffName = data.tariffName || '';
        this.interestRate = data.interestRate || 0;
        this.status = data.status || '';
        this.clientId = data.clientId || '';
    }

    getRemainingAmount() {
        return this.amount - this.paidAmount;
    }

    getFormattedAmount() {
        return `${this.amount} ₽`;
    }

    getFormattedPaidAmount() {
        return `${this.paidAmount} ₽`;
    }

    getFormattedInterestRate() {
        return `${this.interestRate}%`;
    }

    getStatusLabel() {
        const statusMap = {
            ACTIVE: "Активный",
            PAID_OFF: "Погашен",
            OVERDUE: "Просрочен"
        };
        return statusMap[this.status] || "Неизвестный статус";
    }
}
