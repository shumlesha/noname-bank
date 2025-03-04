import { authService } from '../core/authService.js';
import { storageService } from '../core/storageService.js';
import { DomUtils } from '../utils/domUtils.js';
import { config } from '../config/config.js';

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
                errorMessageEl.textContent = 'Пароли не совпадают';
                errorMessageEl.style.display = 'block';
                return;
            }
            
            const roleCheckboxes = DomUtils.findAll('input[name="roles"]:checked');
            const roles = Array.from(roleCheckboxes).map(cb => cb.value);
            
            if (roles.length === 0) {
                errorMessageEl.textContent = 'Выберите хотя бы одну роль';
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
                
                window.location.href = config.routes.login;
            } catch (error) {
                errorMessageEl.textContent = error.message || 'Произошла ошибка при регистрации';
                errorMessageEl.style.display = 'block';
            }
        });
    }
}
