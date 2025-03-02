function initRegisterPage() {
    const registerForm = document.getElementById('register-form');
    const errorMessageEl = document.getElementById('error-message');

    if (!registerForm) return;

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        errorMessageEl.style.display = 'none';

        const email = document.getElementById('email').value;
        const fullName = document.getElementById('fullName').value;
        const gender = document.getElementById('gender').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

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

        const userData = {
            email,
            fullName,
            gender,
            roles,
            password
        };

        try {
            const response = await apiService.register(userData);

            storageService.saveUserData({
                userId: response.data.userId,
                email: response.data.email
            });

            window.location.href = '/client/login';
        } catch (error) {
            errorMessageEl.textContent = error.message || 'Произошла ошибка при регистрации';
            errorMessageEl.style.display = 'block';
        }
    });
}

function initLoginPage() {
    const loginForm = document.getElementById('login-form');
    const errorMessageEl = document.getElementById('error-message');

    if (!loginForm) return;

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorMessageEl.style.display = 'none';

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await apiService.login({ email, password });
            storageService.saveTokens(response.data);
            storageService.saveUserData({
                userId: response.data.userId,
                email: email
            });
            window.location.href = '/client/home';
        } catch (error) {
            errorMessageEl.textContent = error.message || 'Неверный email или пароль';
            errorMessageEl.style.display = 'block';
        }
    });
}
