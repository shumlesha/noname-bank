import { authService } from '../core/authService.js';
import { DomUtils } from '../utils/domUtils.js';
import { config } from '../config/config.js';
import { showError, showSuccess } from '../utils/modalUtils.js';

export class LoginController {
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
        const loginForm = DomUtils.find('#login-form');
        const errorMessageEl = DomUtils.find('#error-message');
        
        if (!loginForm) return;
        
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            errorMessageEl.style.display = 'none';
            
            const email = DomUtils.find('#email').value;
            const password = DomUtils.find('#password').value;
            
            try {
                await authService.login({ email, password });
                window.location.href = config.routes.home;
            } catch (error) {
                const errorMessage = error.message || 'Неверный email или пароль';
                showError(errorMessage);

                errorMessageEl.textContent = errorMessage;
                errorMessageEl.style.display = 'block';
            }
        });
    }
}
