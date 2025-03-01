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

        if (!response.data || response.data.length === 0) {
            accountsTableBody.innerHTML = `<tr><td colspan="5">Нет доступных счетов</td></tr>`;
            return;
        }

        accountsTableBody.innerHTML = response.data.map(account => `
            <tr>
                <td>${account.number}</td>
                <td>${account.balance} ₽</td>
                <td>${account.closedTimestamp ? 'Закрыт' : 'Активен'}</td>
                <td>
                    ${!account.closedTimestamp ?
            `<button onclick="closeAccount('${account.id}')" class="btn btn-danger">Закрыть</button>`
            : '—'}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error("Ошибка при загрузке счетов:", error);
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
