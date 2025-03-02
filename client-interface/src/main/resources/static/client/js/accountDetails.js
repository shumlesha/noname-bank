async function loadAccountDetails() {
    const urlParams = new URLSearchParams(window.location.search);
    const accountId = urlParams.get('accountId');
    const userId = storageService.getUserData()?.userId;

    if (!accountId || !userId) {
        document.getElementById('account-info').innerHTML = '<p>Ошибка загрузки данных</p>';
        return;
    }

    try {
        const accountResponse = await apiService.fetch('/api/query/account', {
            method: 'POST',
            body: JSON.stringify({ clientId: userId, accountId })
        });

        document.getElementById('account-number').innerText = accountResponse.number;
        document.getElementById('account-balance').innerText = `${accountResponse.balance} ₽`;
        document.getElementById('account-status').innerText = accountResponse.closedTimestamp ? 'Закрыт' : 'Активен';
        document.getElementById('account-type').innerText = accountResponse.isCredit ? 'Кредитный' : 'Дебетовый';

        await loadTransactions(accountId);
    } catch (error) {
        console.error("Ошибка загрузки информации о счете:", error);
        document.getElementById('account-info').innerHTML = '<p>Ошибка загрузки данных</p>';
    }
}

async function loadTransactions(accountId) {
    try {
        const transactionResponse = await apiService.fetch('/api/query/transaction/account', {
            method: 'POST',
            body: JSON.stringify({ accountId })
        });

        const transactionTable = document.getElementById('transactions-list');
        transactionTable.innerHTML = transactionResponse.transactions.length
            ? transactionResponse.transactions.map(tx => `
                <tr>
                    <td>${new Date(tx.transactionTimestamp).toLocaleString()}</td>
                    <td>${tx.accountFrom === accountId ? `→ ${tx.accountTo}` : `← ${tx.accountFrom}`}</td>
                    <td>${tx.amount} ₽</td>
                </tr>
            `).join('')
            : '<tr><td colspan="3">Нет транзакций</td></tr>';
    } catch (error) {
        console.error("Ошибка загрузки транзакций:", error);
        document.getElementById('transactions-list').innerHTML = '<tr><td colspan="3">Ошибка загрузки</td></tr>';
    }
}