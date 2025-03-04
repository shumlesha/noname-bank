import storageService from './services/storage-service.js';
import apiService from './services/api-service.js';
import { addCardField } from './utils/ui-utils.js';
import { formatGenderValue } from './utils/format-utils.js';


export const checkAuth = () => {
    const currentPath = window.location.pathname;
    const isLoginPage = currentPath === '/employee/login';
    const isRegisterPage = currentPath === '/employee/register';

    if (!isLoginPage && !isRegisterPage) {
        const tokenData = storageService.getTokens();
        if (!tokenData) {
            window.location.href = '/employee/login';
            return false;
        }
        return true;
    } else if (storageService.getTokens()) {
        window.location.href = '/employee/me';
        return false;
    }

    return true;
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