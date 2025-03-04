import apiService from '../services/api-service.js';
import storageService from '../services/storage-service.js';
import { renderEmployeeCard } from '../auth.js';
import { checkAuth } from '../auth.js';


export const initProfilePage = () => {
    const userDetailsContainer = document.querySelector('.user-details');

    if (!checkAuth()) {
        return;
    }

    apiService.getUserMe()
        .then(response => {
            const userData = response.data;
            storageService.saveUserData(userData);

            if (userDetailsContainer) {
                renderEmployeeCard(userData, userDetailsContainer);
            }
        })
        .catch(error => {
            if (userDetailsContainer) {
                userDetailsContainer.innerHTML = '<p class="error-message">Не удалось загрузить данные пользователя. Пожалуйста, попробуйте позже.</p>';
            }
        });
};