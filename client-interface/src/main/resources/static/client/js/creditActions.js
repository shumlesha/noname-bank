async function loadCredits() {
    const clientId = storageService.getUserData()?.userId;
    fetch(`/api/credit/query/${clientId}`)
        .then(res => res.json())
        .then(data => {
            let creditsList = document.getElementById("credits-list");
            creditsList.innerHTML = "";

            data.data.forEach(credit => {
                let row = `<tr>
                    <td>${credit.id}</td>
                    <td>${credit.amount} ₽</td>
                    <td>${credit.paidAmount} ₽</td>
                    <td>${credit.tariffName}</td>
                    <td>${credit.interestRate}%</td>
                    <td>${credit.status}</td>
                    <td>
                        <button class="btn btn-primary" onclick="payCredit('${credit.id}')">Оплатить</button>
                    </td>
                </tr>`;
                creditsList.innerHTML += row;
            });
        });
}

async function payCredit(creditId) {
    let amount = prompt("Введите сумму оплаты:");
    if (amount) {
        fetch(`/api/credit/command/pay`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ creditId, amount: parseFloat(amount) })
        }).then(() => loadCredits());
    }
}

async function fetchCreditTariffs() {
    fetch(`/api/credit/tariff/query/all`)
        .then(res => res.json())
        .then(data => {
            let tariffsList = document.getElementById("tariffs-list");
            tariffsList.innerHTML = "";

            data.data.forEach(tariff => {
                let row = `<tr>
                    <td>${tariff.name}</td>
                    <td>${tariff.interestRate}%</td>
                    <td>
                        <button class="btn btn-success" onclick="takeCredit('${tariff.id}')">Выбрать</button>
                    </td>
                </tr>`;
                tariffsList.innerHTML += row;
            });

            showModal();
        });
}

async function takeCredit(tariffId) {
    const clientId = storageService.getUserData()?.userId;
    if (!clientId) {
        alert("Ошибка: не удалось получить идентификатор клиента.");
        return;
    }

    let amount = prompt("Введите сумму кредита:");
    if (amount) {
        fetch(`/api/credit/command`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ clientId, amount: parseFloat(amount), tariffId })
        })
            .then(res => {
                if (!res.ok) {
                    return res.json().then(err => { throw new Error(err.message); });
                }
                return res.json();
            })
            .then(() => {
                loadCredits();
                closeModal();
            })
            .catch(err => {
                alert("Ошибка при создании кредита: " + err.message);
            });
    }
}

function showModal() {
    let modal = document.getElementById("tariff-modal");
    modal.style.display = "flex";
    modal.classList.add("show");
}

function closeModal() {
    let modal = document.getElementById("tariff-modal");
    modal.style.display = "none";
    modal.classList.remove("show");
}