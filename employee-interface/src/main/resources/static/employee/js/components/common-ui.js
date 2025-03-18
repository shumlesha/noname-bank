import {logout} from '../auth.js';
import storageService from '../services/storage-service.js';


export const initCommonUI = () => {
    initLogoutButton();

    displayUserEmail();
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
