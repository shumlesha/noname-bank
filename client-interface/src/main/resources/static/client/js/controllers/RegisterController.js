import { authService } from '../core/authService.js';
import { storageService } from '../core/storageService.js';
import { DomUtils } from '../utils/domUtils.js';
import { config } from '../config/config.js';
import { showError, showSuccess } from '../utils/modalUtils.js';

export class RegisterController {
    constructor() {
        this.init();
    }
    
    init() {
        if (authService.isAuthenticated()) {
            window.location.href = config.routes.home;
            return;
        }
        
        this.bindEventListeners();
    }
    
    bindEventListeners() {
        const registerForm = DomUtils.find('#register-form');
        const errorMessageEl = DomUtils.find('#error-message');
        
        if (!registerForm) return;
        
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            errorMessageEl.style.display = 'none';
            
            const email = DomUtils.find('#email').value;
            const fullName = DomUtils.find('#fullName').value;
            const gender = DomUtils.find('#gender').value;
            const password = DomUtils.find('#password').value;
            const confirmPassword = DomUtils.find('#confirmPassword').value;
            
            if (password !== confirmPassword) {
                const errorMessage = 'Пароли не совпадают';
                showError(errorMessage);

                errorMessageEl.textContent = errorMessage;
                errorMessageEl.style.display = 'block';
                return;
            }
            
            const roleCheckboxes = DomUtils.findAll('input[name="roles"]:checked');
            const roles = Array.from(roleCheckboxes).map(cb => cb.value);
            
            if (roles.length === 0) {
                const errorMessage = 'Выберите хотя бы одну роль';
                showError(errorMessage);

                errorMessageEl.textContent = errorMessage;
                errorMessageEl.style.display = 'block';
                return;
            }
            
            const userData = {
                email,
                fullName,
                gender,
                roles,
                password
            };
            
            try {
                const response = await authService.register(userData);
                storageService.saveUserData({
                    userId: response.userId,
                    email: response.email
                });
                
                showSuccess('Регистрация успешно завершена! Перенаправление на страницу входа...');

                setTimeout(() => {
                    window.location.href = config.routes.login;
                }, 1500);
            } catch (error) {
                const errorMessage = error.message || 'Произошла ошибка при регистрации';
                showError(errorMessage);

                errorMessageEl.textContent = errorMessage;
                errorMessageEl.style.display = 'block';
            }
        });
    }
}
