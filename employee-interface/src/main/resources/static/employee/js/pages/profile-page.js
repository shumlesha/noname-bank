import apiService from '../services/api-service.js';
import storageService from '../services/storage-service.js';
import { renderEmployeeCard } from '../auth.js';
import { checkAuth } from '../auth.js';


export const initProfilePage = async () => {
    const userDetailsContainer = document.querySelector('.user-details');

    console.log("starting profile load");
    try {
        const response = await apiService.getUserMe();
        console.log("profile data received:", response);
        const userData = response.data;
        storageService.saveUserData(userData);
        if (userDetailsContainer) {
            renderEmployeeCard(userData, userDetailsContainer);
        }
    } catch (error) {
        console.error("Error loading profile:", error);
        if (userDetailsContainer) {
            userDetailsContainer.innerHTML = '<p class="error-message">Не удалось загрузить данные пользователя. Пожалуйста, попробуйте позже.</p>';
        }
    }
};