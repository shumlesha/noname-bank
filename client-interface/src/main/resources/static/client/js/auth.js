const API_BASE_URL = '/api';
const AUTH_ENDPOINT = `${API_BASE_URL}/auth`;
const TOKEN_STORAGE_KEY = 'auth_tokens';
const USER_STORAGE_KEY = 'user_data';

const API_URLS = {
    register: `${AUTH_ENDPOINT}/register`,
    login: `${AUTH_ENDPOINT}/login`,
    logout: `${AUTH_ENDPOINT}/logout`
};

const storageService = {
    saveTokens: function(tokenData) {
        localStorage.setItem(TOKEN_STORAGE_KEY, JSON.stringify({
            userId: tokenData.userId,
            accessToken: tokenData.accessToken,
            refreshToken: tokenData.refreshToken
        }));
    },

    getTokens: function() {
        const tokensStr = localStorage.getItem(TOKEN_STORAGE_KEY);
        return tokensStr ? JSON.parse(tokensStr) : null;
    },

    removeTokens: function() {
        localStorage.removeItem(TOKEN_STORAGE_KEY);
    },

    saveUserData: function(userData) {
        localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(userData));
    },

    getUserData: function() {
        const userDataStr = localStorage.getItem(USER_STORAGE_KEY);
        return userDataStr ? JSON.parse(userDataStr) : null;
    },

    removeUserData: function() {
        localStorage.removeItem(USER_STORAGE_KEY);
    }
};

const apiService = {
    fetch: async function(url, options = {}) {
        const tokenData = storageService.getTokens();

        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json'
            }
        };

        if (tokenData && tokenData.accessToken && !options.skipAuth) {
            defaultOptions.headers['Authorization'] = `Bearer ${tokenData.accessToken}`;
        }

        const fetchOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...(options.headers || {})
            }
        };

        try {
            const response = await fetch(url, fetchOptions);

            const text = await response.text();
            const data = text.length > 0 ? JSON.parse(text) : null;

            if (!response.ok) {
                throw new Error(data?.message || `Ошибка ${response.status}: ${response.statusText}`);
            }

            return data;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    },

    register: async function(userData) {
        const options = {
            method: 'POST',
            body: JSON.stringify(userData),
            skipAuth: true
        };

        return this.fetch(API_URLS.register, options);
    },

    login: async function(credentials) {
        const options = {
            method: 'POST',
            body: JSON.stringify(credentials),
            skipAuth: true
        };

        return this.fetch(API_URLS.login, options);
    },

    logout: async function() {
        const tokenData = storageService.getTokens();

        if (!tokenData || !tokenData.refreshToken) {
            throw new Error('Токен обновления не найден');
        }

        const options = {
            method: 'POST',
            body: JSON.stringify({
                refreshToken: tokenData.refreshToken
            })
        };

        return this.fetch(API_URLS.logout, options);
    }
};

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
            window.location.href = '/client/home';
        } catch (error) {
            errorMessageEl.textContent = error.message || 'Неверный email или пароль';
            errorMessageEl.style.display = 'block';
        }
    });
}

function initHomePage() {
    const logoutBtn = document.getElementById('logout-btn');
    const userEmailEl = document.getElementById('user-email');
    const userIdEl = document.getElementById('user-id');
    const userEmailDetailsEl = document.getElementById('user-email-details');

    const tokenData = storageService.getTokens();

    if (!tokenData) {
        window.location.href = '/client/login';
        return;
    }

    if (userIdEl) userIdEl.textContent = tokenData.userId;

    const userData = storageService.getUserData();
    if (userData && userData.email) {
        if (userEmailEl) userEmailEl.textContent = userData.email;
        if (userEmailDetailsEl) userEmailDetailsEl.textContent = userData.email;
    }

    logoutBtn.addEventListener('click', async () => {
        try {
            await apiService.logout();
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            storageService.removeTokens();
            storageService.removeUserData();
            window.location.href = '/client/login';
        }
    });
}

function checkAuth() {
    const tokenData = storageService.getTokens();

    if (!tokenData) {
        window.location.href = '/client/login';
        return false;
    }

    return true;
}

document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;

    if (currentPath !== '/client/login' && currentPath !== '/client/register') {
        checkAuth();
    }
});