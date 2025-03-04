import { authService } from '../service/authService.js';
import { storageService } from "../storage/storageService.js";

document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('login-form');
    const errorMessageEl = document.getElementById('error-message');
    if (!loginForm) return;

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorMessageEl.style.display = 'none';

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await authService.login({ email, password });
            console.log(response);
            window.location.href = '/client/home';
        } catch (error) {
            errorMessageEl.textContent = error.message || 'Неверный email или пароль';
            errorMessageEl.style.display = 'block';
            throw error;
        }
    });
});
