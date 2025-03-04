import { authService } from '../service/authService.js';
import { accountService } from '../service/accountService.js';
import { storageService } from '../storage/storageService.js';

document.addEventListener('DOMContentLoaded', function () {
    const logoutBtn = document.getElementById('logout-btn');
    const userEmailEl = document.getElementById('user-email');
    const userIdEl = document.getElementById('user-id');
    const userEmailDetailsEl = document.getElementById('user-email-details');

    const tokenData = storageService.getTokens();

    if (!tokenData) {
        window.location.href = '/client/login';
        return;
    }
    if (userIdEl) userIdEl.textContent = tokenData.userId;
    const userData = storageService.getUserData();

    if (userData && userData.email) {
        if (userEmailEl) userEmailEl.textContent = userData.email;
        if (userEmailDetailsEl) userEmailDetailsEl.textContent = userData.email;
    }

    if (!userData) {
        window.location.href = '/client/login';
        return;
    }

    document.getElementById('user-email').textContent = userData.email;

    logoutBtn.addEventListener('click', async () => {
        await authService.logout();
        storageService.removeTokens();
        storageService.getUserData();
        window.location.href = '/client/login';
    });
});

document.addEventListener('DOMContentLoaded', async function () {
    const userId = storageService.getUserData()?.userId;
    if (!userId) return;

    await loadAccounts();
});

document.addEventListener('DOMContentLoaded', function () {
    const createAccountBtn = document.getElementById('create-account-btn');

    if (createAccountBtn) {
        createAccountBtn.addEventListener('click', async function () {
            await createAccount();
        });
    }
});

async function createAccount() {
    const userId = storageService.getUserData()?.userId;

    if (!userId) {
        alert("Ошибка: Пользователь не найден");
        return;
    }

    if (!confirm("Вы действительно хотите создать новый счет?")) return;

    try {
        await accountService.createAccount(userId);
        alert("Новый счет успешно создан!");
        await loadAccounts();
    } catch (error) {
        console.error("Ошибка при создании счета:", error);
        alert("Ошибка при создании счета");
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
        const { account, transactions } = await accountService.loadAccountDetails(userId, accountId);

        accountDetailsContainer.innerHTML = `
            <h3>Детали счета</h3>
            <p><strong>Номер:</strong> ${account.number}</p>
            <p><strong>Баланс:</strong> ${account.balance} ₽</p>
            <p><strong>Тип:</strong> ${account.isCredit ? 'Кредитный' : 'Дебетовый'}</p>
            <p><strong>Статус:</strong> ${account.closedTimestamp ? 'Закрыт' : 'Активен'}</p>
        `;

        transactionsTableBody.innerHTML = transactions.length
            ? transactions.map(tx => `
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

async function loadAccounts() {
    const userId = storageService.getUserData()?.userId;
    const accountsTableBody = document.getElementById("accounts-list");

    if (!userId) {
        accountsTableBody.innerHTML = `<tr><td colspan="5">Ошибка загрузки данных</td></tr>`;
        return;
    }

    try {
        const response = await accountService.loadAccounts(userId);

        accountsTableBody.innerHTML = response.data.length
            ? response.data.map(account => `
                <tr>
                    <td><a href="#" class="account-link" data-account-id="${account.id}">${account.number}</a></td>
                    <td>${account.balance} ₽</td>
                    <td>${account.isCredit ? 'Кредитный' : 'Дебетовый'}</td>
                    <td>${account.closedTimestamp ? 'Закрыт' : 'Активен'}</td>
                    <td>
                        ${!account.closedTimestamp ? `
                            <button class="btn btn-success deposit-btn" data-account-id="${account.id}">Пополнить</button>
                            <button class="btn btn-warning withdraw-btn" data-account-id="${account.id}">Снять</button>
                            <button class="btn btn-danger close-btn" data-account-id="${account.id}">Закрыть</button>
                        ` : '—'}
                    </td>
                </tr>
            `).join('')
            : `<tr><td colspan="5">Нет доступных счетов</td></tr>`;

        bindEventListeners();
        bindAccountLinks();

    } catch (error) {
        console.error("Ошибка загрузки счетов:", error);
        accountsTableBody.innerHTML = `<tr><td colspan="5">Ошибка загрузки данных</td></tr>`;
    }
}

function bindAccountLinks() {
    document.querySelectorAll('.account-link').forEach(link => {
        link.addEventListener('click', function (event) {
            event.preventDefault();
            const accountId = this.getAttribute('data-account-id');
            loadAccountDetails(accountId);
        });
    });
}

function bindEventListeners() {
    document.querySelectorAll('.deposit-btn').forEach(button => {
        button.addEventListener('click', function () {
            const accountId = button.getAttribute('data-account-id');
            depositMoney(accountId);
        });
    });

    document.querySelectorAll('.withdraw-btn').forEach(button => {
        button.addEventListener('click', function () {
            const accountId = button.getAttribute('data-account-id');
            withdrawMoney(accountId);
        });
    });

    document.querySelectorAll('.close-btn').forEach(button => {
        button.addEventListener('click', function () {
            const accountId = button.getAttribute('data-account-id');
            closeAccount(accountId);
        });
    });
}

document.addEventListener('DOMContentLoaded', function () {
    const backToAccountsBtn = document.getElementById('back-to-accounts-btn');

    if (backToAccountsBtn) {
        backToAccountsBtn.addEventListener('click', function () {
            document.getElementById("account-details-section").style.display = "none";
            document.getElementById("accounts-section").style.display = "block";
        });
    }
});

async function depositMoney(accountId) {
    console.log("TEST DEPOSIT");
    const amount = prompt("Введите сумму для пополнения:");
    if (!amount || isNaN(amount) || amount <= 0) {
        alert("Некорректная сумма");
        return;
    }

    try {
        await accountService.depositMoney(amount, accountId);
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
        await accountService.withdrawMoney(amount, accountId);
        alert(`Со счета снято ${amount} ₽`);
        await loadAccounts();
    } catch (error) {
        console.error("Ошибка при снятии денег:", error);
        alert("Ошибка при снятии денег");
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
        await accountService.closeAccount(userId, accountId);
        alert(`Счет закрыт! Номер: ${accountId}`);
        await loadAccounts();
    } catch (error) {
        console.error("Ошибка при закрытии счета:", error);
        alert("Ошибка при закрытии счета");
    }
}
