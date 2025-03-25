import apiService from '../services/api-service.js';
import {checkAuth} from '../auth.js';
import {renderClientInfo} from '../components/client-info.js';
import {renderAccountsList} from '../components/accounts-list.js';
import {renderAccountDetails} from '../components/account-details.js';
import {
    initTransactionsWebSocket,
    cleanupTransactionsWebSocket
} from '../components/transactions-list.js';
import {renderCreditsList} from '../components/credits-list.js';
import {renderCreditRating} from '../components/credit-rating.js';
import {renderMissedPaymentsList} from '../components/missed-payments-list.js';
import {clearMessages, hideElement, showElement, showMessage} from '../utils/ui-utils.js';
import {TabbedModal} from '../components/modal.js';

let currentAccountId = null;
let accountDetailsModal = null;

export const initClientDetailsPage = () => {
    if (window.clientDetailsPageInitialized) return;
    window.clientDetailsPageInitialized = true;

    if (!checkAuth()) {
        console.error('Пользователь не авторизован');
        return;
    }

    clearMessages();

    const clientId = getClientIdFromUrl();
    if (!clientId) {
        showMessage('ID клиента не указан', 'error');
        return;
    }

    loadClientDetails(clientId);
    loadClientAccounts(clientId);
    loadClientCredits(clientId);
    loadClientCreditRating(clientId);
    loadClientMissedPayments(clientId);

    window.addEventListener('beforeunload', () => {
        if (currentAccountId) {
            cleanupTransactionsWebSocket();
            currentAccountId = null;
        }
    });
};

const getClientIdFromUrl = () => {
    const urlParts = window.location.pathname.split('/');
    return urlParts[urlParts.length - 1];
};

const loadClientDetails = async (clientId) => {
    const clientInfoContainer = document.getElementById('client-info-container');
    const loadingIndicator = document.getElementById('client-loading');

    showElement(loadingIndicator);

    try {
        const response = await apiService.getUserById(clientId);
        hideElement(loadingIndicator);

        if (response && response.data) {
            const clientData = response.data.data || response.data;
            renderClientInfo(clientData, clientInfoContainer);
            document.title = `${clientData.fullName} | Банк NoName`;
        } else {
            showMessage('Не удалось загрузить информацию о клиенте', 'error');
            clientInfoContainer.innerHTML = '';
        }
    } catch (error) {
        console.error('Ошибка при загрузке информации о клиенте:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при загрузке информации о клиенте', 'error');
        clientInfoContainer.innerHTML = '';
    }
};

const loadClientAccounts = async (clientId) => {
    const accountsContainer = document.getElementById('client-accounts-container');
    const loadingIndicator = document.getElementById('client-accounts-loading');

    showElement(loadingIndicator);

    try {
        const response = await apiService.getClientAccounts(clientId);
        hideElement(loadingIndicator);

        if (response && response.data) {
            let accounts = [];

            if (Array.isArray(response.data)) {
                accounts = response.data;
            } else if (response.data.data && Array.isArray(response.data.data)) {
                accounts = response.data.data;
            } else {
                console.error('Неожиданный формат данных:', response.data);
                showMessage('Неожиданный формат данных от сервера', 'error');
                accountsContainer.innerHTML = '';
                return;
            }
            
            renderAccountsList(accounts, accountsContainer, (accountId, clientId) => {
                loadAccountDetails(accountId, clientId);
            });
        } else {
            showMessage('Не удалось загрузить список счетов клиента', 'error');
            accountsContainer.innerHTML = '';
        }
    } catch (error) {
        console.error('Ошибка при загрузке счетов клиента:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при загрузке счетов клиента', 'error');
        accountsContainer.innerHTML = '';
    }
};

const loadClientCredits = async (clientId) => {
    const creditsContainer = document.getElementById('client-credits-container');
    const loadingIndicator = document.getElementById('client-credits-loading');

    showElement(loadingIndicator);

    try {
        const response = await apiService.getClientCredits(clientId);
        hideElement(loadingIndicator);

        if (response && response.data) {
            let credits = [];

            if (Array.isArray(response.data)) {
                credits = response.data;
            } else if (response.data.data && Array.isArray(response.data.data)) {
                credits = response.data.data;
            } else {
                console.error('Неожиданный формат данных кредитов:', response.data);
                showMessage('Неожиданный формат данных кредитов от сервера', 'error');
                creditsContainer.innerHTML = '';
                return;
            }
            
            renderCreditsList(credits, creditsContainer);
        } else {
            showMessage('Не удалось загрузить список кредитов клиента', 'error');
            creditsContainer.innerHTML = '';
        }
    } catch (error) {
        console.error('Ошибка при загрузке кредитов клиента:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при загрузке кредитов клиента', 'error');
        creditsContainer.innerHTML = '';
    }
};

const loadClientCreditRating = async (clientId) => {
    const creditRatingContainer = document.getElementById('client-credit-rating-container');
    const loadingIndicator = document.getElementById('client-credit-rating-loading');

    if (!creditRatingContainer) return;

    showElement(loadingIndicator);

    try {
        const response = await apiService.getCreditRatingByClient(clientId);
        hideElement(loadingIndicator);

        if (response && response.data) {
            let ratingData = null;

            if (response.data.data) {
                ratingData = response.data.data;
            } else {
                ratingData = response.data;
            }
            
            renderCreditRating(ratingData, creditRatingContainer);
        } else {
            creditRatingContainer.innerHTML = '<div class="error-message-container"><p class="info-message">Данные о кредитном рейтинге не найдены</p></div>';
        }
    } catch (error) {
        console.error('Ошибка при загрузке кредитного рейтинга:', error);
        hideElement(loadingIndicator);
        creditRatingContainer.innerHTML = '<div class="error-message-container"><p class="error-message">Ошибка при загрузке кредитного рейтинга</p></div>';
    }
};

const loadClientMissedPayments = async (clientId) => {
    const missedPaymentsContainer = document.getElementById('client-missed-payments-container');
    const loadingIndicator = document.getElementById('client-missed-payments-loading');

    if (!missedPaymentsContainer) return;

    showElement(loadingIndicator);

    try {
        const response = await apiService.getMissedPaymentsByClient(clientId);
        hideElement(loadingIndicator);

        if (response && response.data) {
            let missedPayments = [];

            if (Array.isArray(response.data)) {
                missedPayments = response.data;
            } else if (response.data.data && Array.isArray(response.data.data)) {
                missedPayments = response.data.data;
            }
            
            renderMissedPaymentsList(missedPayments, missedPaymentsContainer);
        } else {
            missedPaymentsContainer.innerHTML = '<div class="error-message-container"><p class="info-message">Просроченные платежи не найдены</p></div>';
        }
    } catch (error) {
        console.error('Ошибка при загрузке просроченных платежей:', error);
        hideElement(loadingIndicator);
        missedPaymentsContainer.innerHTML = '<div class="error-message-container"><p class="error-message">Ошибка при загрузке просроченных платежей</p></div>';
    }
};

const loadAccountDetails = async (accountId, clientId) => {
    const loadingIndicator = document.getElementById('account-details-loading');

    if (currentAccountId && currentAccountId !== accountId) {
        cleanupTransactionsWebSocket();
    }

    currentAccountId = accountId;


    if (!accountDetailsModal) {
        accountDetailsModal = new TabbedModal({
            title: 'Детали счета',
            width: '800px',
            tabs: [
                { title: 'Информация о счете', content: '<div id="modal-account-details"></div>' },
                { title: 'Транзакции', content: '<div id="modal-transactions"></div>' }
            ],
            onClose: () => {
                if (currentAccountId) {
                    cleanupTransactionsWebSocket();
                    currentAccountId = null;
                }
            },
            footerButtons: [
                {
                    id: 'close-modal',
                    text: 'Закрыть',
                    class: 'modal-btn-secondary',
                    handler: () => {
                        accountDetailsModal.close();
                    }
                }
            ]
        });
    }


    accountDetailsModal.open();
    showElement(loadingIndicator);

    try {
        const response = await apiService.getAccountDetails(accountId, clientId);
        hideElement(loadingIndicator);

        const accountData = response.data ? response.data : response;

        if (accountData && accountData.id) {
            const accountDetailsContainer = document.getElementById('modal-account-details');
            if (accountDetailsContainer) {
                renderAccountDetails(accountData, accountDetailsContainer);
                accountDetailsModal.setTitle(`Счет: ${accountData.number || accountId}`);
            }
            
            loadAccountTransactions(accountId);
        } else {
            console.error('Данные счета не содержат необходимых полей:', response);
            showMessage('Данные счета неполные или в неверном формате', 'error');
            accountDetailsModal.close();
        }
    } catch (error) {
        console.error('Ошибка при загрузке деталей счета:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при загрузке деталей счета', 'error');
        accountDetailsModal.close();
    }
};

const loadAccountTransactions = (accountId) => {
    const transactionsContainer = document.getElementById('modal-transactions');
    
    if (!transactionsContainer) return;
    

    initTransactionsWebSocket(accountId, transactionsContainer);
};
