import apiService from '../services/api-service.js';
import storageService from '../services/storage-service.js';
import { checkAuth } from '../auth.js';


export const initRegisterPage = () => {
    const registerForm = document.getElementById('register-form');
    const errorMessageEl = document.getElementById('error-message');

    if (!registerForm) return;

    checkAuth();

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        errorMessageEl.style.display = 'none';

        const email = document.getElementById('email').value;
        const fullName = document.getElementById('fullName').value;
        const genderRadio = document.querySelector('input[name="gender"]:checked');
        const gender = genderRadio ? genderRadio.value : null;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (!gender) {
            errorMessageEl.textContent = 'Выберите пол (мужской или женский)';
            errorMessageEl.style.display = 'block';
            return;
        }

        if (gender !== 'MALE' && gender !== 'FEMALE') {
            errorMessageEl.textContent = 'Выберите допустимый пол (мужской или женский)';
            errorMessageEl.style.display = 'block';
            return;
        }

        if (password !== confirmPassword) {
            errorMessageEl.textContent = 'Пароли не совпадают';
            errorMessageEl.style.display = 'block';
            return;
        }

        const roleCheckboxes = document.querySelectorAll('input[name="roles"]:checked');
        const roles = Array.from(roleCheckboxes).map(cb => cb.value);

        if (roles.length === 0) {
            errorMessageEl.textContent = 'Выберите хотя бы одну роль';
            errorMessageEl.style.display = 'block';
            return;
        }

        const userData = { email, fullName, gender, roles, password };

        try {
            const response = await apiService.register(userData);
            storageService.saveUserData({ userId: response.data.userId, email: response.data.email });
            window.location.href = '/employee/login';
        } catch (error) {
            errorMessageEl.textContent = error.message || 'Произошла ошибка при регистрации';
            errorMessageEl.style.display = 'block';
        }
    });
};