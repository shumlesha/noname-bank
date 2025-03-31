import {DomUtils} from '../utils/domUtils.js';
import {accountService} from '../core/accountService.js';
import {showError} from '../utils/modalUtils.js';

export class AccountDetailsComponent {
    constructor(parentController) {
        this.parentController = parentController;
        this.currentAccountId = null;
        this.transactionUpdateCallback = null;
    }

    async loadAccountDetails(accountId) {
        const userData = this.parentController.getUserData();
        if (!userData) return;

        this.currentAccountId = accountId;

        try {
            const data = await accountService.loadAccountDetails(userData.userId, accountId);
            
            const accountDetailsEl = DomUtils.find("#account-details");
            
            accountDetailsEl.innerHTML = `
                <h3>Детали счета</h3>
                <p><strong>ID счета:</strong> ${data.account.id}</p>
                <p><strong>Номер счета:</strong> ${data.account.number}</p>
                <p><strong>Баланс:</strong> ${data.account.getFormattedBalance()}</p>
                <p><strong>Валюта:</strong> ${data.account.currency}</p>
                <p><strong>Тип:</strong> ${data.account.getType()}</p>
                <p><strong>Статус:</strong> ${data.account.getStatus()}</p>
            `;
            
            this.renderTransactions(data.transactions, data.account.id);
            
            this.subscribeToTransactionUpdates(accountId);
            
            DomUtils.find('#accounts-section').style.display = 'none';
            DomUtils.find('#account-details-section').style.display = 'block';
        } catch (error) {
            console.error("Ошибка загрузки деталей счета:", error);
            showError("Ошибка загрузки деталей счета.");
        }
    }
    
    subscribeToTransactionUpdates(accountId) {
        if (this.transactionUpdateCallback) {
            accountService.unsubscribeFromTransactions(this.currentAccountId, this.transactionUpdateCallback);
        }
        
        this.transactionUpdateCallback = (transactions) => {
            this.renderTransactions(transactions, accountId);
        };
        
        accountService.subscribeToTransactions(accountId, this.transactionUpdateCallback);
    }
    
    unsubscribeFromTransactionUpdates() {
        if (this.transactionUpdateCallback && this.currentAccountId) {
            accountService.unsubscribeFromTransactions(this.currentAccountId, this.transactionUpdateCallback);
            this.transactionUpdateCallback = null;
        }
    }

    renderTransactions(transactions, currentAccountId) {
        const transactionsList = DomUtils.find('#transactions-list');
        if (!transactions || transactions.length === 0) {
            transactionsList.innerHTML = '<tr><td colspan="5">Нет транзакций</td></tr>';
            return;
        }

        const transactionsHtml = transactions.map(transaction => {
            let type = '';
            let direction = '';
            let amount = '';
            
            if (transaction.accountFrom === '') {
                type = 'Пополнение';
                direction = `Пополнение на счет`;
                amount = `+${transaction.amount}`;
            } else if (transaction.accountTo === '') {
                type = 'Снятие';
                direction = `Снятие со счета`;
                amount = `-${transaction.amount}`;
            } else {
                type = 'Перевод';
                if (transaction.accountTo === currentAccountId) {
                    direction = `Перевод со счета ${transaction.accountFrom} на текущий счет`;
                    amount = `+${transaction.amount}`;
                } else {
                    direction = `Перевод с текущего счета на счет ${transaction.accountTo}`;
                    amount = `-${transaction.amount}`;
                }
            }

            return `
                <tr>
                    <td>${transaction.id}</td>
                    <td>${new Date(transaction.transactionTimestamp).toLocaleString()}</td>
                    <td>${type}</td>
                    <td>${direction}</td>
                    <td class="${amount.startsWith('+') ? 'positive-amount' : 'negative-amount'}">${amount}</td>
                </tr>
            `;
        }).join('');

        transactionsList.innerHTML = transactionsHtml;
    }

    backToAccountList() {
        this.unsubscribeFromTransactionUpdates();
        DomUtils.find('#account-details-section').style.display = 'none';
        DomUtils.find('#accounts-section').style.display = 'block';
    }
} 