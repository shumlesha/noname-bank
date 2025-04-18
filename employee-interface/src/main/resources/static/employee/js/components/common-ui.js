import {logout} from '../auth.js';
import storageService from '../services/storage-service.js';
import themeService from '../services/theme-service.js';
import pushService from "../services/push-service.js";


export const initCommonUI = async () => {
    initLogoutButton();
    initThemeToggle();
    displayUserEmail();

    await themeService.init();
    await pushService.init();
};


const initLogoutButton = () => {
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
};


const displayUserEmail = () => {
    const userEmailEl = document.getElementById('user-email');
    if (userEmailEl) {
        const userData = storageService.getUserData();
        if (userData && userData.email) {
            userEmailEl.textContent = userData.email;
        }
    }
};


const initThemeToggle = () => {
    const themeToggleBtn = document.getElementById('theme-toggle-btn');
    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', themeService.toggleTheme);
    }
};
