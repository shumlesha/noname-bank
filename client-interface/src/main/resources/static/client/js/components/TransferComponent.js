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
            const loadingMessage = document.createElement('div');
            loadingMessage.className = 'loading-message';
            loadingMessage.innerHTML = `
                <div class="loading-spinner"></div>
                <p>Выполняется перевод...</p>
            `;
            document.body.appendChild(loadingMessage);
            
            await accountService.transferMoney(fromAccountId, toAccountId, amount);

            loadingMessage.innerHTML = `
                <div class="success-icon">✓</div>
                <p>Перевод успешно выполнен!</p>
                <p>Переход к истории транзакций счета...</p>
            `;
            
            showSuccess("Перевод успешно выполнен");

            setTimeout(async () => {
                document.body.removeChild(loadingMessage);
                
                if (this.parentController.reloadAccounts) {
                    await this.parentController.reloadAccounts();
                }

                if (this.parentController.loadAccountDetails) {
                    this.parentController.loadAccountDetails(fromAccountId);
                }
            }, 1500);
        } catch (error) {
            const loadingMessage = document.querySelector('.loading-message');
            if (loadingMessage) {
                document.body.removeChild(loadingMessage);
            }
            
            console.error("Ошибка при переводе:", error);
            showError(error.message || "Ошибка при переводе средств");
        }
    }
} 