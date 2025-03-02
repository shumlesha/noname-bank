const ACCOUNTS_PER_PAGE = 10;
let currentPage = 0;
let totalAccountsCount = 0;
let allLoadedAccounts = [];

document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname === '/employee/accounts') {
        initAccountsPage();
    }
});

function initAccountsPage() {
    if (window.accountsPageInitialized) return;
    window.accountsPageInitialized = true;
    if (!checkAuth()) {
        console.error('Пользователь не авторизован');
        return;
    }

    const searchForm = document.getElementById('search-form'),
        searchInput = document.getElementById('search-input'),
        backToListBtn = document.getElementById('back-to-list-btn');

    clearMessages();
    hideElement(backToListBtn);
    loadAccounts(0, ACCOUNTS_PER_PAGE);


    searchForm.addEventListener('submit', e => {
        e.preventDefault();
        const clientIdQuery = searchInput.value.trim();
        if (clientIdQuery) {
            filterAccountsByClientId(clientIdQuery);
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
        searchInput.value = '';
        loadAccounts(0, ACCOUNTS_PER_PAGE);
    });
}

const loadAccounts = async (page, pageSize) => {
    const accountsContainer = document.getElementById('accounts-container'),
        loadingIndicator = document.getElementById('accounts-loading'),
        paginationContainer = document.getElementById('pagination-container');

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
            
            renderAccountsList(accounts, accountsContainer);
            const totalPages = Math.ceil(totalAccountsCount / pageSize);
            renderPagination(paginationContainer, page, totalPages);
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

const loadClientAccounts = async (clientId) => {
    const accountsContainer = document.getElementById('accounts-container'),
        loadingIndicator = document.getElementById('accounts-loading'),
        paginationContainer = document.getElementById('pagination-container'),
        backToListBtn = document.getElementById('back-to-list-btn');

    showElement(loadingIndicator);
    showElement(backToListBtn);

    try {
        const response = await apiService.getClientAccounts(clientId);
        hideElement(loadingIndicator);

        let accounts = [];
        if (response && response.data) {
            accounts = Array.isArray(response.data)
                ? response.data
                : (response.data.data || []);

            if (accounts.length > 0) {
                renderAccountsList(accounts, accountsContainer);
                showMessage(`Найдено ${accounts.length} счетов для клиента ${clientId}`, 'success');
            } else {
                showMessage('Счета для указанного клиента не найдены', 'warning');
                accountsContainer.innerHTML = '';
            }
            hideElement(paginationContainer);
        } else {
            showMessage('Счета для указанного клиента не найдены', 'warning');
            accountsContainer.innerHTML = '';
        }
    } catch (error) {
        console.error('Ошибка при поиске счетов клиента:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при поиске счетов клиента', 'error');
        accountsContainer.innerHTML = '';
    }
};

const loadAccountDetails = async (accountId, clientId) => {
    const accountsContainer = document.getElementById('accounts-container'),
        paginationContainer = document.getElementById('pagination-container'),
        accountDetailsContainer = document.getElementById('account-details-container'),
        loadingIndicator = document.getElementById('account-details-loading'),
        backToListBtn = document.getElementById('back-to-list-btn');

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

const loadAccountTransactions = async (accountId) => {
    const transactionsContainer = document.getElementById('transactions-container'),
        loadingIndicator = document.getElementById('transactions-loading');

    if (!transactionsContainer) return;

    console.log('Loading transactions for account ID:', accountId);
    showElement(loadingIndicator);
    transactionsContainer.style.display = 'block';
    transactionsContainer.innerHTML = '<h3>Транзакции по счету</h3><div class="loading-indicator"><span class="loading-text">Загрузка транзакций...</span></div>';

    try {
        const response = await apiService.getAccountTransactions(accountId);
        console.log('Raw transaction response:', response);
        hideElement(loadingIndicator);


        if (response && response.transactions && Array.isArray(response.transactions)) {
            console.log('Found transactions array in response:', response.transactions);
            renderTransactionsList(response.transactions, transactionsContainer);
        } else if (response && response.data && response.data.transactions && Array.isArray(response.data.transactions)) {
            console.log('Found transactions array in response.data:', response.data.transactions);
            renderTransactionsList(response.data.transactions, transactionsContainer);
        } else if (Array.isArray(response)) {
            console.log('Response is a direct array of transactions:', response);
            renderTransactionsList(response, transactionsContainer);
        } else {
            console.error('Неожиданный формат данных транзакций:', response);
            transactionsContainer.innerHTML = '<h3>Транзакции по счету</h3><p class="info-message">Транзакции не найдены</p>';
        }
    } catch (error) {
        console.error('Ошибка при загрузке транзакций:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при загрузке транзакций', 'error');
        transactionsContainer.innerHTML = '<h3>Транзакции по счету</h3>';
    }
};

const renderAccountsList = (accounts, container) => {
    if (!accounts || !accounts.length) {
        container.innerHTML = '<div class="error-message-container"><p class="info-message">Счета не найдены</p></div>';
        return;
    }

    const table = document.createElement('table');
    table.className = 'accounts-table';
    table.innerHTML = `
    <thead>
      <tr>
        <th>Номер счета</th>
        <th>Клиент</th>
        <th>Баланс</th>
        <th>Тип</th>
        <th>Статус</th>
        <th>Действия</th>
      </tr>
    </thead>
    <tbody>
      ${accounts.map(account => {
        const status = account.closedTimestamp
            ? 'Закрыт'
            : account.blockedTimestamp
                ? 'Заблокирован'
                : 'Активен';
        const accountType = account.isCredit ? 'Кредитный' : 'Дебетовый';
        const number = account.number || 'Нет данных';
        const clientId = account.clientId || 'Нет данных';
        const balance = account.balance !== undefined
            ? account.balance.toLocaleString('ru-RU') + ' ₽'
            : 'Нет данных';
        return `<tr>
          <td>${number}</td>
          <td>${clientId}</td>
          <td>${balance}</td>
          <td>${accountType}</td>
          <td>${status}</td>
          <td>
            <button class="btn btn-details" data-account-id="${account.id}" data-client-id="${account.clientId}">
              Детали
            </button>
          </td>
        </tr>`;
    }).join('')}
    </tbody>
  `;
    container.innerHTML = '';
    container.appendChild(table);

    container.querySelectorAll('.btn-details').forEach(button => {
        button.addEventListener('click', function() {
            loadAccountDetails(this.getAttribute('data-account-id'), this.getAttribute('data-client-id'));
        });
    });
};

const renderAccountDetails = (account, container) => {
    if (!account || typeof account !== 'object') {
        container.innerHTML = '<p class="error-message">Некорректные данные счета</p>';
        return;
    }

    const status = account.closedTimestamp
        ? 'Закрыт'
        : account.blockedTimestamp
            ? 'Заблокирован'
            : 'Активен';
    const accountType = account.isCredit ? 'Кредитный' : 'Дебетовый';
    const creationDate = account.creationTimestamp ? new Date(account.creationTimestamp).toLocaleString('ru-RU') : 'Нет данных';
    const blockedDate = account.blockedTimestamp ? new Date(account.blockedTimestamp).toLocaleString('ru-RU') : 'Нет';
    const closedDate = account.closedTimestamp ? new Date(account.closedTimestamp).toLocaleString('ru-RU') : 'Нет';

    const detailsHTML = `
    <h3>Детали счета</h3>
    <div class="account-info">
      <p><strong>ID счета:</strong> ${account.id || 'Нет данных'}</p>
      <p><strong>Номер счета:</strong> ${account.number || 'Нет данных'}</p>
      <p><strong>ID клиента:</strong> ${account.clientId || 'Нет данных'}</p>
      <p><strong>Баланс:</strong> ${account.balance !== undefined ? account.balance.toLocaleString('ru-RU') + ' ₽' : 'Нет данных'}</p>
      <p><strong>Тип счета:</strong> ${accountType}</p>
      <p><strong>Статус:</strong> ${status}</p>
      <p><strong>Дата открытия:</strong> ${creationDate}</p>
      <p><strong>Дата блокировки:</strong> ${blockedDate}</p>
      <p><strong>Дата закрытия:</strong> ${closedDate}</p>
    </div>
  `;
    container.innerHTML = detailsHTML;
};

const renderTransactionsList = (transactions, container) => {
    console.log('Rendering transactions list:', transactions);
    
    if (!transactions || !transactions.length) {
        container.innerHTML = '<h3>Транзакции по счету</h3><div class="error-message-container"><p class="info-message">Транзакции не найдены</p></div>';
        return;
    }

    const table = document.createElement('table');
    table.className = 'transactions-table';
    table.innerHTML = `
    <thead>
      <tr>
        <th>ID транзакции</th>
        <th>Дата и время</th>
        <th>Счет отправителя</th>
        <th>Счет получателя</th>
        <th>Сумма</th>
      </tr>
    </thead>
    <tbody>
      ${transactions.map(tx => {
        try {
            const transactionDate = tx.transactionTimestamp ? new Date(tx.transactionTimestamp).toLocaleString('ru-RU') : 'Нет данных';
            const accountFrom = tx.accountFrom || 'Пополнение';
            const accountTo = tx.accountTo || 'Нет данных';
            const amount = tx.amount !== undefined ? tx.amount.toLocaleString('ru-RU') + ' ₽' : 'Нет данных';
            return `<tr>
              <td>${tx.id || 'Нет данных'}</td>
              <td>${transactionDate}</td>
              <td>${accountFrom}</td>
              <td>${accountTo}</td>
              <td>${amount}</td>
            </tr>`;
        } catch (error) {
            console.error('Error rendering transaction:', tx, error);
            return `<tr><td colspan="5">Ошибка отображения транзакции</td></tr>`;
        }
    }).join('')}
    </tbody>
  `;
    container.innerHTML = '<h3>Транзакции по счету</h3>';
    container.appendChild(table);
};

const renderPagination = (container, currentPage, totalPages) => {
    container.innerHTML = '';
    if (totalPages <= 1) {
        hideElement(container);
        return;
    }

    showElement(container, 'flex');
    const prevButton = createButton('Назад', currentPage === 0, () => {
        if (currentPage > 0) loadAccounts(currentPage - 1, ACCOUNTS_PER_PAGE);
    });
    container.appendChild(prevButton);


    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages - 1, currentPage + 2);

    const pagesShown = endPage - startPage + 1;
    if (pagesShown < 5) {
        if (startPage === 0) {
            endPage = Math.min(totalPages - 1, endPage + (5 - pagesShown));
        } else if (endPage === totalPages - 1) {
            startPage = Math.max(0, startPage - (5 - pagesShown));
        }
    }

    if (startPage > 0) {
        const firstPageBtn = createButton(1, false, () => loadAccounts(0, ACCOUNTS_PER_PAGE));
        container.appendChild(firstPageBtn);

        if (startPage > 1) {
            const ellipsis = document.createElement('span');
            ellipsis.className = 'pagination-ellipsis';
            ellipsis.textContent = '...';
            container.appendChild(ellipsis);
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        const btn = createButton(i + 1, i === currentPage, () => loadAccounts(i, ACCOUNTS_PER_PAGE));
        if (i === currentPage) btn.classList.add('active');
        container.appendChild(btn);
    }

    if (endPage < totalPages - 1) {
        if (endPage < totalPages - 2) {
            const ellipsis = document.createElement('span');
            ellipsis.className = 'pagination-ellipsis';
            ellipsis.textContent = '...';
            container.appendChild(ellipsis);
        }

        const lastPageBtn = createButton(totalPages, false, () => loadAccounts(totalPages - 1, ACCOUNTS_PER_PAGE));
        container.appendChild(lastPageBtn);
    }

    const nextButton = createButton('Вперед', currentPage === totalPages - 1, () => {
        if (currentPage < totalPages - 1) loadAccounts(currentPage + 1, ACCOUNTS_PER_PAGE);
    });
    container.appendChild(nextButton);
};

const createButton = (text, disabled, onClick) => {
    const btn = document.createElement('button');
    btn.className = 'btn btn-pagination';
    btn.textContent = text;
    btn.disabled = disabled;
    btn.addEventListener('click', onClick);
    return btn;
};

function showMessage(message, type = 'info') {
    const messagesContainer = document.getElementById('messages-container');
    if (!messagesContainer) return;

    const messageDiv = document.createElement('div');
    messageDiv.className = 'error-message-container';
    const p = document.createElement('p');
    p.className = `${type}-message`;
    p.textContent = message;
    messageDiv.appendChild(p);

    messagesContainer.innerHTML = '';
    messagesContainer.appendChild(messageDiv);

    if (type === 'info' || type === 'success') {
        setTimeout(() => {
            messageDiv.style.opacity = '0';
            setTimeout(() => {
                if (messagesContainer.contains(messageDiv)) {
                    messagesContainer.removeChild(messageDiv);
                }
            }, 500);
        }, 5000);
    }
}

function clearMessages() {
    const messagesContainer = document.getElementById('messages-container');
    if (messagesContainer) messagesContainer.innerHTML = '';
}

function showElement(element, displayStyle = 'block') {
    if (element) element.style.display = displayStyle;
}

function hideElement(element) {
    if (element) element.style.display = 'none';
}


function filterAccountsByClientId(clientIdQuery) {
    const accountsContainer = document.getElementById('accounts-container'),
        paginationContainer = document.getElementById('pagination-container'),
        backToListBtn = document.getElementById('back-to-list-btn');
    

    showElement(backToListBtn);
    

    if (!allLoadedAccounts || allLoadedAccounts.length === 0) {
        loadAccounts(0, ACCOUNTS_PER_PAGE);
        return;
    }
    

    const filteredAccounts = allLoadedAccounts.filter(account => 
        account.clientId && account.clientId.toString().includes(clientIdQuery)
    );
    

    if (filteredAccounts.length > 0) {
        renderAccountsList(filteredAccounts, accountsContainer);
    } else {
        accountsContainer.innerHTML = '';
    }
    

    hideElement(paginationContainer);
}
