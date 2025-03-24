import storageService from './services/storage-service.js';
import apiService from './services/api-service.js';
import { addCardField } from './utils/ui-utils.js';
import { formatGenderValue } from './utils/format-utils.js';
import API_CONFIG from "./config/api-config.js";


export const checkAuth = async () => {
    try {
        const response = await apiService.getUserMe();
        if (response.status === "OK") {
            return true;
        } else {
            //window.location.href = 'http://localhost:8080/';
            console.log('response.status:', response.status);
            return false;
        }
    } catch (error) {
        console.error('Error checking user me:', error);
        return false;
    }
};


export const renderEmployeeCard = (userData, container) => {
    if (!container) return;

    container.innerHTML = '';

    const card = document.createElement('div');
    card.className = 'employee-card';

    addCardField(card, 'ФИО', userData.fullName || 'Не указано');
    addCardField(card, 'Email', userData.email);

    if (userData.gender) {
        addCardField(card, 'Пол', formatGenderValue(userData.gender));
    }

    addCardField(card, 'Статус', userData.banned ? 'Заблокирован' : 'Активен');

    container.appendChild(card);
};


export const logout = async () => {
    try {
        await apiService.logout();
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        storageService.removeTokens();
        storageService.removeUserData();
        window.location.href = '/employee/login';
    }
};