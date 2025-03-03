async function loadAccounts() {
    const userId = storageService.getUserData()?.userId;
    const accountsTableBody = document.getElementById("accounts-list");

    if (!userId) {
        accountsTableBody.innerHTML = `<tr><td colspan="5">Ошибка загрузки данных</td></tr>`;
        return;
    }

    try {
        const response = await apiService.fetch('/api/query/account/list', {
            method: 'POST',
            body: JSON.stringify({ clientId: userId })
        });

        accountsTableBody.innerHTML = response.data.length
            ? response.data.map(account => `
                <tr>
                    <td><a href="#" onclick="loadAccountDetails('${account.id}')" class="account-link">${account.number}</a></td>
                    <td>${account.balance} ₽</td>
                    <td>${account.isCredit ? 'Кредитный' : 'Дебетовый'}</td>
                    <td>${account.closedTimestamp ? 'Закрыт' : 'Активен'}</td>
                    <td>
                        ${!account.closedTimestamp ? `
                            <button onclick="depositMoney('${account.id}')" class="btn btn-success">Пополнить</button>
                            <button onclick="withdrawMoney('${account.id}')" class="btn btn-warning">Снять</button>
                            <button onclick="closeAccount('${account.id}')" class="btn btn-danger">Закрыть</button>
                        ` : '—'}
                    </td>
                </tr>
            `).join('')
            : `<tr><td colspan="5">Нет доступных счетов</td></tr>`;
    } catch (error) {
        console.error("Ошибка загрузки счетов:", error);
        accountsTableBody.innerHTML = `<tr><td colspan="5">Ошибка загрузки данных</td></tr>`;
    }
}

async function createAccount() {
    const userId = storageService.getUserData()?.userId;

    if (!userId) {
        alert("Ошибка: Пользователь не найден");
        return;
    }

    try {
        const response = await apiService.fetch('/api/account/create', {
            method: 'POST',
            body: JSON.stringify({ clientId: userId })
        });

        alert(`Счет создан! Номер: ${response.accountNumber}`);
        await loadAccounts();
    } catch (error) {
        console.error("Ошибка при создании счета:", error);
        alert("Ошибка при создании счета");
    }
}

async function closeAccount(accountId) {
    const userId = storageService.getUserData()?.userId;

    if (!userId) {
        alert("Ошибка: Пользователь не найден");
        return;
    }

    if (!confirm("Вы уверены, что хотите закрыть этот счет?")) return;

    try {
        const response = await apiService.fetch('/api/account/close', {
            method: 'POST',
            body: JSON.stringify({ clientId: userId, accountId: accountId })
        });

        alert(`Счет закрыт! Номер: ${response.accountNumber}`);
        await loadAccounts();
    } catch (error) {
        console.error("Ошибка при закрытии счета:", error);
        alert("Ошибка при закрытии счета");
    }
}

async function depositMoney(accountId) {
    const amount = prompt("Введите сумму для пополнения:");
    if (!amount || isNaN(amount) || amount <= 0) {
        alert("Некорректная сумма");
        return;
    }

    try {
        await apiService.fetch('/api/atm/deposit', {
            method: 'POST',
            body: JSON.stringify({ amount, accountId })
        });

        alert(`Счет пополнен на ${amount} ₽`);
        await loadAccounts();
    } catch (error) {
        console.error("Ошибка при пополнении счета:", error);
        alert("Ошибка при пополнении счета");
    }
}

async function withdrawMoney(accountId) {
    const amount = prompt("Введите сумму для снятия:");
    if (!amount || isNaN(amount) || amount <= 0) {
        alert("Некорректная сумма");
        return;
    }

    try {
        await apiService.fetch('/api/atm/withdraw', {
            method: 'POST',
            body: JSON.stringify({ amount, accountId })
        });

        alert(`Со счета снято ${amount} ₽`);
        await loadAccounts();
    } catch (error) {
        console.error("Ошибка при снятии денег:", error);
        alert("Ошибка при снятии денег");
    }
}

async function loadAccountDetails(accountId) {
    const userId = storageService.getUserData()?.userId;
    const accountDetailsContainer = document.getElementById("account-details");
    const transactionsTableBody = document.getElementById("transactions-list");

    if (!userId) {
        alert("Ошибка: Пользователь не найден");
        return;
    }

    try {
        const accountResponse = await apiService.fetch('/api/query/account', {
            method: 'POST',
            body: JSON.stringify({ clientId: userId, accountId })
        });

        const transactionsResponse = await apiService.fetch('/api/query/transaction/account', {
            method: 'POST',
            body: JSON.stringify({ accountId })
        });

        accountDetailsContainer.innerHTML = `
            <h3>Детали счета</h3>
            <p><strong>Номер:</strong> ${accountResponse.number}</p>
            <p><strong>Баланс:</strong> ${accountResponse.balance} ₽</p>
            <p><strong>Тип:</strong> ${accountResponse.isCredit ? 'Кредитный' : 'Дебетовый'}</p>
            <p><strong>Статус:</strong> ${accountResponse.closedTimestamp ? 'Закрыт' : 'Активен'}</p>
        `;

        transactionsTableBody.innerHTML = transactionsResponse.transactions.length
            ? transactionsResponse.transactions.map(tx => `
                <tr>
                    <td>${tx.transactionTimestamp}</td>
                    <td>${tx.accountFrom === accountId ? '-' : '+'} ${tx.amount} ₽</td>
                    <td>${tx.accountFrom === accountId ? 'Списание' : 'Пополнение'}</td>
                </tr>
            `).join('')
            : `<tr><td colspan="3">Нет транзакций</td></tr>`;

        document.getElementById("accounts-section").style.display = "none";
        document.getElementById("account-details-section").style.display = "block";
    } catch (error) {
        console.error("Ошибка загрузки данных о счете:", error);
        alert("Ошибка загрузки данных о счете");
    }
}

function showAccountsList() {
    document.getElementById("account-details-section").style.display = "none";
    document.getElementById("accounts-section").style.display = "block";
}

function goToCredits() {
    window.location.href = "/client/credit";
}
