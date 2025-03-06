import API_CONFIG from '../config/api-config.js';
import apiService from '../services/api-service.js';
import { checkAuth } from '../auth.js';
import { renderTariffsList } from '../components/tariffs-list.js';
import { showMessage, clearMessages, showElement, hideElement } from '../utils/ui-utils.js';

export const initCreditTariffsPage = () => {
    if (window.creditTariffsPageInitialized) return;
    window.creditTariffsPageInitialized = true;

    if (!checkAuth()) {
        console.error('Пользователь не авторизован');
        return;
    }

    clearMessages();
    

    loadCreditTariffs();
    

    initEventHandlers();
};

const loadCreditTariffs = async () => {
    const tariffsContainer = document.getElementById('tariffs-container');
    const loadingIndicator = document.getElementById('tariffs-loading');

    showElement(loadingIndicator);

    try {
        const response = await apiService.getAllCreditTariffs();
        hideElement(loadingIndicator);

        if (response && response.data) {
            let tariffs = [];

            if (Array.isArray(response.data)) {
                tariffs = response.data;
            } else if (response.data.data && Array.isArray(response.data.data)) {
                tariffs = response.data.data;
            } else {
                console.error('Неожиданный формат данных:', response.data);
                showMessage('Неожиданный формат данных от сервера', 'error');
                tariffsContainer.innerHTML = '';
                return;
            }

            renderTariffsList(tariffs, tariffsContainer, editTariff, showDeleteConfirmation);
        } else {
            showMessage('Не удалось загрузить список тарифов', 'error');
            tariffsContainer.innerHTML = '';
        }
    } catch (error) {
        console.error('Ошибка при загрузке тарифов:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при загрузке тарифов', 'error');
        tariffsContainer.innerHTML = '';
    }
};

const initEventHandlers = () => {
    const createTariffBtn = document.getElementById('create-tariff-btn');
    if (createTariffBtn) {
        createTariffBtn.addEventListener('click', () => {
            showTariffForm();
        });
    }


    const closeFormBtn = document.getElementById('close-form-btn');
    if (closeFormBtn) {
        closeFormBtn.addEventListener('click', () => {
            hideTariffForm();
        });
    }


    const cancelBtn = document.getElementById('cancel-btn');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            hideTariffForm();
        });
    }


    const tariffForm = document.getElementById('tariff-form');
    if (tariffForm) {
        tariffForm.addEventListener('submit', (e) => {
            e.preventDefault();
            saveTariff();
        });
    }


    const closeModalBtn = document.getElementById('close-modal-btn');
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', () => {
            hideDeleteConfirmation();
        });
    }

    const cancelDeleteBtn = document.getElementById('cancel-delete-btn');
    if (cancelDeleteBtn) {
        cancelDeleteBtn.addEventListener('click', () => {
            hideDeleteConfirmation();
        });
    }

    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');
    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', () => {
            deleteTariff();
        });
    }
};

const showTariffForm = (tariffData = null) => {
    const formTitle = document.getElementById('form-title');
    const tariffIdInput = document.getElementById('tariff-id');
    const tariffNameInput = document.getElementById('tariff-name');
    const tariffInterestRateInput = document.getElementById('tariff-interest-rate');
    const tariffAutoPaymentRateInput = document.getElementById('tariff-autopayment-rate');
    const tariffPenaltyRateInput = document.getElementById('tariff-penalty-rate');

    if (tariffData) {
        formTitle.textContent = 'Редактирование тарифа';
        tariffIdInput.value = tariffData.id;
        tariffNameInput.value = tariffData.name;
        tariffInterestRateInput.value = tariffData.interestRate;
        tariffAutoPaymentRateInput.value = tariffData.autoPaymentRate || 0;
        tariffPenaltyRateInput.value = tariffData.penaltyRate || 0;
    } else {
        formTitle.textContent = 'Создание нового тарифа';
        tariffIdInput.value = '';
        tariffNameInput.value = '';
        tariffInterestRateInput.value = '';
        tariffAutoPaymentRateInput.value = '';
        tariffPenaltyRateInput.value = '';
    }

    showElement('tariff-form-container');
};

const hideTariffForm = () => {
    const formContainer = document.getElementById('tariff-form-container');
    hideElement(formContainer);
};

const editTariff = async (tariffId) => {
    try {
        const response = await apiService.getCreditTariffById(tariffId);
        
        if (response && response.data) {
            const tariffData = response.data.data || response.data;
            showTariffForm(tariffData);
        } else {
            showMessage('Не удалось загрузить данные тарифа', 'error');
        }
    } catch (error) {
        console.error('Ошибка при загрузке данных тарифа:', error);
        showMessage('Произошла ошибка при загрузке данных тарифа', 'error');
    }
};

const saveTariff = async () => {
    const tariffIdInput = document.getElementById('tariff-id');
    const tariffNameInput = document.getElementById('tariff-name');
    const tariffInterestRateInput = document.getElementById('tariff-interest-rate');
    const tariffAutoPaymentRateInput = document.getElementById('tariff-autopayment-rate');
    const tariffPenaltyRateInput = document.getElementById('tariff-penalty-rate');

    const tariffId = tariffIdInput.value.trim();
    const name = tariffNameInput.value.trim();
    const interestRate = parseFloat(tariffInterestRateInput.value);
    const autoPaymentRate = parseFloat(tariffAutoPaymentRateInput.value);
    const penaltyRate = parseFloat(tariffPenaltyRateInput.value);

    if (!name) {
        showMessage('Название тарифа не может быть пустым', 'error');
        return;
    }

    if (isNaN(interestRate) || interestRate < 0) {
        showMessage('Процентная ставка должна быть положительным числом', 'error');
        return;
    }

    if (isNaN(autoPaymentRate) || autoPaymentRate < 0) {
        showMessage('Автоматический платеж должен быть положительным числом', 'error');
        return;
    }

    if (isNaN(penaltyRate) || penaltyRate < 0) {
        showMessage('Штрафная ставка должна быть положительным числом', 'error');
        return;
    }

    try {
        let response;
        
        if (tariffId) {
            response = await apiService.updateCreditTariff({
                tariffId,
                name,
                interestRate,
                autoPaymentRate,
                penaltyRate
            });
            showMessage('Тариф успешно обновлен', 'success');
        } else {
            response = await apiService.createCreditTariff({
                name,
                interestRate,
                autoPaymentRate,
                penaltyRate
            });
            showMessage('Тариф успешно создан', 'success');
        }

        hideTariffForm();
        loadCreditTariffs();
    } catch (error) {
        console.error('Ошибка при сохранении тарифа:', error);
        showMessage('Произошла ошибка при сохранении тарифа', 'error');
    }
};

const showDeleteConfirmation = (tariffId, tariffName) => {
    const modal = document.getElementById('confirmation-modal');
    const deleteTariffNameSpan = document.getElementById('delete-tariff-name');
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');

    deleteTariffNameSpan.textContent = tariffName;
    confirmDeleteBtn.setAttribute('data-tariff-id', tariffId);
    
    showElement(modal);
};

const hideDeleteConfirmation = () => {
    const modal = document.getElementById('confirmation-modal');
    hideElement(modal);
};

const deleteTariff = async () => {
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');
    const tariffId = confirmDeleteBtn.getAttribute('data-tariff-id');

    if (!tariffId) {
        showMessage('ID тарифа не указан', 'error');
        return;
    }

    try {
        await apiService.deleteCreditTariff(tariffId);
        showMessage('Тариф успешно удален', 'success');
        hideDeleteConfirmation();
        loadCreditTariffs();
    } catch (error) {
        console.error('Ошибка при удалении тарифа:', error);
        showMessage('Произошла ошибка при удалении тарифа', 'error');
    }
};
