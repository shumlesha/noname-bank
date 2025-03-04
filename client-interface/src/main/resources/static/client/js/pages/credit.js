import { storageService } from "../storage/storageService.js";
import { creditService } from "../service/creditService.js";
import { closeModal, showModal } from "../components/modal.js";

document.addEventListener("DOMContentLoaded", () => {
    loadCredits();
});

document.getElementById('fetch-tariffs-btn').addEventListener('click', async function () {
    await fetchCreditTariffs();
});

document.getElementById('back-btn').addEventListener('click', function () {
    window.location.href = "/client/home";
});

document.getElementById('logout-btn').addEventListener('click', function () {
    storageService.removeUserData();
    storageService.removeTokens();
    window.location.href = "/client/login";
});

async function loadCredits() {
    const clientId = storageService.getUserData()?.userId;
    if (!clientId) {
        alert("Ошибка: не удалось получить идентификатор клиента.");
        return;
    }

    try {
        const data = await creditService.loadCredits(clientId);
        let creditsList = document.getElementById("credits-list");
        creditsList.innerHTML = "";

        if (data.data.length === 0) {
            creditsList.innerHTML = "<tr><td colspan='7'>Нет активных кредитов</td></tr>";
            return;
        }

        data.data.forEach(credit => {
            let row = `<tr>
                <td>${credit.id}</td>
                <td>${credit.amount} ₽</td>
                <td>${credit.paidAmount} ₽</td>
                <td>${credit.tariffName}</td>
                <td>${credit.interestRate}%</td>
                <td>${credit.status}</td>
                <td>
                    <button class="btn btn-primary pay-btn" data-id="${credit.id}">Оплатить</button>
                </td>
            </tr>`;
            creditsList.innerHTML += row;
        });

        document.querySelectorAll(".pay-btn").forEach(button => {
            button.addEventListener("click", function () {
                payCredit(this.dataset.id);
            });
        });

    } catch (error) {
        console.error("Ошибка загрузки кредитов:", error);
    }
}

async function payCredit(creditId) {
    let amount = prompt("Введите сумму оплаты:");
    if (!amount) return;

    try {
        await creditService.payCredit(creditId, amount);
        loadCredits();
    } catch (error) {
        alert("Ошибка при оплате кредита.");
    }
}

async function fetchCreditTariffs() {
    try {
        const data = await creditService.fetchCreditTariffs();
        let tariffsList = document.getElementById("tariffs-list");
        tariffsList.innerHTML = "";

        if (data.data.length === 0) {
            tariffsList.innerHTML = "<tr><td colspan='3'>Нет доступных тарифов</td></tr>";
            return;
        }

        data.data.forEach(tariff => {
            let row = `<tr>
                <td>${tariff.name}</td>
                <td>${tariff.interestRate}%</td>
                <td>
                    <button class="btn btn-success take-credit-btn" data-id="${tariff.id}">Выбрать</button>
                </td>
            </tr>`;
            tariffsList.innerHTML += row;
        });

        document.querySelectorAll(".take-credit-btn").forEach(button => {
            button.addEventListener("click", function () {
                takeCredit(this.dataset.id);
            });
        });

        showModal();
    } catch (error) {
        console.error("Ошибка загрузки тарифов:", error);
    }
}

async function takeCredit(tariffId) {
    const clientId = storageService.getUserData()?.userId;
    if (!clientId) {
        alert("Ошибка: не удалось получить идентификатор клиента.");
        return;
    }

    let amount = prompt("Введите сумму кредита:");
    if (!amount) return;

    try {
        await creditService.takeCredit(clientId, tariffId, amount);
        await loadCredits();
        closeModal();
    } catch (err) {
        alert("Ошибка при создании кредита: " + err.message);
    }
}
