import apiService from '../services/api-service.js';
import {checkAuth} from '../auth.js';
import {renderClientInfo} from '../components/client-info.js';
import {renderAccountsList} from '../components/accounts-list.js';
import {renderAccountDetails} from '../components/account-details.js';
import {
    renderTransactionsList,
    initTransactionsWebSocket,
    cleanupTransactionsWebSocket
} from '../components/transactions-list.js';
import {renderCreditsList} from '../components/credits-list.js';
import {renderClientSummary} from '../components/client-summary.js';
import {clearMessages, hideElement, showElement, showMessage} from '../utils/ui-utils.js';

let currentAccountId = null;

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
    

    window.clientData = {
        accounts: null,
        credits: null
    };
    

    loadClientAccounts(clientId);
    loadClientCredits(clientId);

    document.addEventListener('clientDataUpdated', updateClientSummary);

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

            window.clientData.accounts = accounts;
            
            renderAccountsList(accounts, accountsContainer, (accountId, clientId) => {
                loadAccountDetails(accountId, clientId);
            });

            document.dispatchEvent(new CustomEvent('clientDataUpdated'));
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

            window.clientData.credits = credits;
            
            renderCreditsList(credits, creditsContainer);

            document.dispatchEvent(new CustomEvent('clientDataUpdated'));
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

const updateClientSummary = () => {
    const summaryContainer = document.getElementById('client-summary-container');
    if (!summaryContainer) return;

    summaryContainer.innerHTML = '';

    if (window.clientData.accounts && window.clientData.credits) {
        renderClientSummary(window.clientData.accounts, window.clientData.credits, summaryContainer);
    }
};

const loadAccountDetails = async (accountId, clientId) => {
    const accountsContainer = document.getElementById('client-accounts-container');
    const accountDetailsContainer = document.getElementById('account-details-container');
    const loadingIndicator = document.getElementById('account-details-loading');

    if (currentAccountId && currentAccountId !== accountId) {
        cleanupTransactionsWebSocket();
    }

    currentAccountId = accountId;

    showElement(loadingIndicator);
    hideElement(accountsContainer);
    showElement(accountDetailsContainer);

    accountDetailsContainer.innerHTML = '';

    try {
        const response = await apiService.getAccountDetails(accountId, clientId);
        hideElement(loadingIndicator);

        const accountData = response.data ? response.data : response;

        if (accountData && accountData.id) {
            renderAccountDetails(accountData, accountDetailsContainer);
            loadAccountTransactions(accountId);

            const backButton = document.createElement('button');
            backButton.className = 'btn back-button';
            backButton.textContent = 'Вернуться к списку счетов';
            backButton.addEventListener('click', () => {
                if (currentAccountId) {
                    cleanupTransactionsWebSocket();
                    currentAccountId = null;
                }
                
                hideElement(accountDetailsContainer);
                hideElement(document.getElementById('transactions-container'));
                showElement(accountsContainer);
            });

            accountDetailsContainer.insertBefore(backButton, accountDetailsContainer.firstChild);
        } else {
            console.error('Данные счета не содержат необходимых полей:', response);
            showMessage('Данные счета неполные или в неверном формате', 'error');
            accountDetailsContainer.innerHTML = '';
        }
    } catch (error) {
        console.error('Ошибка при загрузке деталей счета:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при загрузке деталей счета', 'error');
        accountDetailsContainer.innerHTML = '';
    }
};

const loadAccountTransactions = (accountId) => {
    const transactionsContainer = document.getElementById('transactions-container');

    if (!transactionsContainer) return;

    console.log('Loading transactions for account ID:', accountId);
    showElement(transactionsContainer, 'block');
    

    initTransactionsWebSocket(accountId, transactionsContainer);
};
