import {DomUtils} from '../utils/domUtils.js';
import {accountService} from '../core/accountService.js';
import {showSuccess, showError} from '../utils/modalUtils.js';

export class TransferComponent {
    constructor(parentController) {
        this.parentController = parentController;
    }

    showTransferModal(accounts) {
        const fromAccountSelect = DomUtils.find('#from-account-select');
        fromAccountSelect.innerHTML = '';
        
        const activeAccounts = accounts.filter(account => account.isActive());
        
        if (activeAccounts.length === 0) {
            showError("У вас нет активных счетов для перевода");
            return;
        }

        activeAccounts.forEach(account => {
            const option = document.createElement('option');
            option.value = account.id;
            option.textContent = `${account.id} (${account.getFormattedBalance()}, ${account.currency})`;
            fromAccountSelect.appendChild(option);
        });

        DomUtils.find('#to-account-id').value = '';
        DomUtils.find('#transfer-amount').value = '';
        
        DomUtils.showModal('transfer-modal');
    }
    
    async processTransfer() {
        const fromAccountId = DomUtils.find('#from-account-select').value;
        const toAccountId = DomUtils.find('#to-account-id').value;
        const amount = DomUtils.find('#transfer-amount').value;
        
        if (!fromAccountId || !toAccountId || !amount) {
            showError("Пожалуйста, заполните все поля");
            return;
        }
        
        if (fromAccountId === toAccountId) {
            showError("Нельзя перевести деньги на тот же счет");
            return;
        }
        
        DomUtils.hideModal('transfer-modal');
        
        try {
            await accountService.transferMoney(fromAccountId, toAccountId, amount);
            showSuccess("Перевод успешно выполнен");

            setTimeout(async () => {
                if (this.parentController.reloadAccounts) {
                    await this.parentController.reloadAccounts();
                }
            }, 1000);
        } catch (error) {
            console.error("Ошибка при переводе:", error);
            showError(error.message || "Ошибка при переводе средств");
        }
    }
} 