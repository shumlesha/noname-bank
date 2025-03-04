import apiService from '../services/api-service.js';
import storageService from '../services/storage-service.js';
import { checkAuth } from '../auth.js';


export const initLoginPage = () => {
    const loginForm = document.getElementById('login-form');
    const errorMessageEl = document.getElementById('error-message');

    if (!loginForm) return;

    checkAuth();

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        errorMessageEl.style.display = 'none';

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await apiService.login({ email, password });
            storageService.saveTokens(response.data);
            window.location.href = '/employee/me';
        } catch (error) {
            errorMessageEl.textContent = error.message || 'Неверный email или пароль';
            errorMessageEl.style.display = 'block';
        }
    });
};