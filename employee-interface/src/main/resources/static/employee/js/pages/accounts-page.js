import API_CONFIG from '../config/api-config.js';
import apiService from '../services/api-service.js';
import {checkAuth} from '../auth.js';
import {renderAccountsList} from '../components/accounts-list.js';
import {renderAccountDetails} from '../components/account-details.js';
import {
    renderTransactionsList, 
    initTransactionsWebSocket,
    cleanupTransactionsWebSocket
} from '../components/transactions-list.js';
import {renderPagination} from '../components/pagination.js';
import {clearMessages, hideElement, showElement, showMessage} from '../utils/ui-utils.js';

const ACCOUNTS_PER_PAGE = API_CONFIG.PAGINATION.ACCOUNTS_PER_PAGE;

let currentPage = 0;
let totalAccountsCount = 0;
let allLoadedAccounts = [];
let currentAccountId = null;


export const initAccountsPage = () => {
    if (window.accountsPageInitialized) return;
    window.accountsPageInitialized = true;

    if (!checkAuth()) {
        console.error('Пользователь не авторизован');
        return;
    }

    const searchForm = document.getElementById('search-form');
    const searchInput = document.getElementById('search-input');
    const backToListBtn = document.getElementById('back-to-list-btn');

    clearMessages();
    hideElement(backToListBtn);

    loadAccounts(0, ACCOUNTS_PER_PAGE);

    searchForm.addEventListener('submit', e => {
        e.preventDefault();
        const clientIdQuery = searchInput.value.trim();

        if (clientIdQuery) {
            filterLocalAccountsByClientId(clientIdQuery);
        } else {
            hideElement(backToListBtn);
            loadAccounts(0, ACCOUNTS_PER_PAGE);
        }
    });

    backToListBtn.addEventListener('click', () => {
        clearMessages();
        showElement(document.getElementById('accounts-container'));
        showElement(document.getElementById('pagination-container'), 'flex');
        hideElement(document.getElementById('account-details-container'));
        hideElement(document.getElementById('transactions-container'));
        hideElement(backToListBtn);

        if (currentAccountId) {
            cleanupTransactionsWebSocket();
            currentAccountId = null;
        }

        searchInput.value = '';
        loadAccounts(0, ACCOUNTS_PER_PAGE);
    });

    window.addEventListener('beforeunload', () => {
        if (currentAccountId) {
            cleanupTransactionsWebSocket();
            currentAccountId = null;
        }
    });
};


const loadAccounts = async (page, pageSize) => {
    const accountsContainer = document.getElementById('accounts-container');
    const loadingIndicator = document.getElementById('accounts-loading');
    const paginationContainer = document.getElementById('pagination-container');

    if (!accountsContainer) return;

    currentPage = page;

    showElement(loadingIndicator);
    showElement(accountsContainer);
    showElement(paginationContainer, 'flex');

    try {
        const response = await apiService.getAllAccounts(pageSize, page * pageSize);
        hideElement(loadingIndicator);

        if (response && response.data) {
            let accounts = [];

            if (Array.isArray(response.data)) {
                accounts = response.data;
                totalAccountsCount = accounts.length;
            } else if (response.data.data && Array.isArray(response.data.data)) {
                accounts = response.data.data;
                totalAccountsCount = response.data.total || accounts.length;
            } else {
                console.error('Неожиданный формат данных:', response.data);
                showMessage('Неожиданный формат данных от сервера', 'error');
                accountsContainer.innerHTML = '';
                return;
            }

            allLoadedAccounts = accounts;

            renderAccountsList(accounts, accountsContainer, (accountId, clientId) => {
                loadAccountDetails(accountId, clientId);
            });

            const totalPages = Math.ceil(totalAccountsCount / pageSize);
            renderPagination(paginationContainer, page, totalPages, (newPage) => {
                loadAccounts(newPage, ACCOUNTS_PER_PAGE);
            });
        } else {
            showMessage('Не удалось загрузить список счетов', 'error');
            accountsContainer.innerHTML = '';
        }
    } catch (error) {
        console.error('Ошибка при загрузке счетов:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при загрузке счетов', 'error');
        accountsContainer.innerHTML = '';
    }
};

const filterLocalAccountsByClientId = (clientIdQuery) => {
    const accountsContainer = document.getElementById('accounts-container');
    const paginationContainer = document.getElementById('pagination-container');
    const backToListBtn = document.getElementById('back-to-list-btn');

    showElement(backToListBtn);
    hideElement(paginationContainer);

    if (!allLoadedAccounts || allLoadedAccounts.length === 0) {
        loadAccounts(0, ACCOUNTS_PER_PAGE);
        return;
    }

    const filteredAccounts = allLoadedAccounts.filter(account =>
        account.clientId && account.clientId.toString().includes(clientIdQuery)
    );

    renderAccountsList(filteredAccounts, accountsContainer, (accountId, clientId) => {
        loadAccountDetails(accountId, clientId);
    });
};


const loadAccountDetails = async (accountId, clientId) => {
    const accountsContainer = document.getElementById('accounts-container');
    const paginationContainer = document.getElementById('pagination-container');
    const accountDetailsContainer = document.getElementById('account-details-container');
    const loadingIndicator = document.getElementById('account-details-loading');
    const backToListBtn = document.getElementById('back-to-list-btn');

    if (currentAccountId && currentAccountId !== accountId) {
        cleanupTransactionsWebSocket();
    }

    currentAccountId = accountId;

    showElement(backToListBtn);
    showElement(loadingIndicator);
    hideElement(accountsContainer);
    hideElement(paginationContainer);
    showElement(accountDetailsContainer);

    accountDetailsContainer.innerHTML = '';

    try {
        const response = await apiService.getAccountDetails(accountId, clientId);
        hideElement(loadingIndicator);

        const accountData = response.data ? response.data : response;

        if (accountData && accountData.id) {
            renderAccountDetails(accountData, accountDetailsContainer);
            loadAccountTransactions(accountId);
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