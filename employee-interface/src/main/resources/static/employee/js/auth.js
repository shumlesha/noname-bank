const API_BASE_URL = '/api';
const AUTH_ENDPOINT = `${API_BASE_URL}/auth`;
const USERS_ENDPOINT = `${API_BASE_URL}/users`;
const QUERY_ENDPOINT = `${API_BASE_URL}/query`;
const TOKEN_STORAGE_KEY = 'auth_tokens';
const USER_STORAGE_KEY = 'user_data';

const API_URLS = {
    register: `${AUTH_ENDPOINT}/register`,
    login: `${AUTH_ENDPOINT}/login`,
    logout: `${AUTH_ENDPOINT}/logout`,
    me: `${USERS_ENDPOINT}/me`,
    accountAll: `${QUERY_ENDPOINT}/account/all`,
    accountDetails: `${QUERY_ENDPOINT}/account`,
    accountList: `${QUERY_ENDPOINT}/account/list`,
    transactionClient: `${QUERY_ENDPOINT}/transaction/client`,
    transactionAccount: `${QUERY_ENDPOINT}/transaction/account`,
    transaction: `${QUERY_ENDPOINT}/transaction`
};

const storageService = {
    saveTokens: (tokenData) => {
        if (!tokenData) {
            console.error('Token data is null or undefined');
            return;
        }
        if (!tokenData.accessToken) console.error('Token data missing accessToken:', tokenData);
        if (!tokenData.refreshToken) console.error('Token data missing refreshToken:', tokenData);
        if (!tokenData.userId) console.error('Token data missing userId:', tokenData);

        const tokenToSave = {
            userId: tokenData.userId,
            accessToken: tokenData.accessToken,
            refreshToken: tokenData.refreshToken
        };
        localStorage.setItem(TOKEN_STORAGE_KEY, JSON.stringify(tokenToSave));
    },

    getTokens: () => {
        const tokensStr = localStorage.getItem(TOKEN_STORAGE_KEY);
        if (!tokensStr) return null;
        try {
            const tokens = JSON.parse(tokensStr);
            if (!tokens.accessToken) console.error('Retrieved tokens missing accessToken:', tokens);
            if (!tokens.refreshToken) console.error('Retrieved tokens missing refreshToken:', tokens);
            if (!tokens.userId) console.error('Retrieved tokens missing userId:', tokens);
            return tokens;
        } catch (error) {
            console.error('Error parsing tokens from localStorage:', error);
            return null;
        }
    },

    removeTokens: () => localStorage.removeItem(TOKEN_STORAGE_KEY),

    saveUserData: (userData) => localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(userData)),

    getUserData: () => {
        const userDataStr = localStorage.getItem(USER_STORAGE_KEY);
        return userDataStr ? JSON.parse(userDataStr) : null;
    },

    removeUserData: () => localStorage.removeItem(USER_STORAGE_KEY)
};

const apiService = {
    fetch: async (url, options = {}) => {
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

        if (fetchOptions.skipAuth) delete fetchOptions.skipAuth;

        try {
            const response = await fetch(url, fetchOptions);
            let data;
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                const text = await response.text();
                data = { message: text };
            }

            if (!response.ok) {
                console.error('Ошибка API:', data);
                throw new Error(data.message || `Ошибка сервера: ${response.status}`);
            }
            return data;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    },

    register: async (userData) => {
        const options = {
            method: 'POST',
            body: JSON.stringify({
                email: userData.email,
                fullName: userData.fullName,
                gender: userData.gender,
                roles: userData.roles,
                password: userData.password
            }),
            skipAuth: true
        };
        return apiService.fetch(API_URLS.register, options);
    },

    login: async (credentials) => {
        const options = {
            method: 'POST',
            body: JSON.stringify(credentials),
            skipAuth: true
        };
        return apiService.fetch(API_URLS.login, options);
    },

    logout: async () => {
        const options = { method: 'POST' };
        return apiService.fetch(API_URLS.logout, options);
    },

    getUserMe: async () => {
        const options = { method: 'GET' };
        try {
            return await apiService.fetch(API_URLS.me, options);
        } catch (error) {
            console.error('Error in getUserMe:', error);
            throw error;
        }
    },

    getAllAccounts: async (pageSize = 10, pageOffset = 0) => {
        return apiService.fetch(API_URLS.accountAll, {
            method: 'POST',
            body: JSON.stringify({ size: pageSize, offset: pageOffset })
        });
    },

    getAccountDetails: async (accountId, clientId) => {
        return apiService.fetch(API_URLS.accountDetails, {
            method: 'POST',
            body: JSON.stringify({ accountId, clientId })
        });
    },

    getClientAccounts: async (clientId) => {
        return apiService.fetch(API_URLS.accountList, {
            method: 'POST',
            body: JSON.stringify({ clientId })
        });
    },

    getAccountTransactions: async (accountId) => {
        return apiService.fetch(API_URLS.transactionAccount, {
            method: 'POST',
            body: JSON.stringify({ accountId })
        });
    },

    getClientTransactions: async (clientId) => {
        return apiService.fetch(API_URLS.transactionClient, {
            method: 'POST',
            body: JSON.stringify({ clientId })
        });
    },

    getTransactionDetails: async (transactionId, clientId) => {
        return apiService.fetch(API_URLS.transaction, {
            method: 'POST',
            body: JSON.stringify({ transactionId, clientId })
        });
    }
};

const initRegisterPage = () => {
    const registerForm = document.getElementById('register-form');
    const errorMessageEl = document.getElementById('error-message');
    if (!registerForm) return;

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorMessageEl.style.display = 'none';

        const email = document.getElementById('email').value,
            fullName = document.getElementById('fullName').value,
            genderRadio = document.querySelector('input[name="gender"]:checked'),
            gender = genderRadio ? genderRadio.value : null,
            password = document.getElementById('password').value,
            confirmPassword = document.getElementById('confirmPassword').value;

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

const initLoginPage = () => {
    const loginForm = document.getElementById('login-form');
    const errorMessageEl = document.getElementById('error-message');
    if (!loginForm) return;

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorMessageEl.style.display = 'none';

        const email = document.getElementById('email').value,
            password = document.getElementById('password').value;

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

const initHomePage = () => {
    const logoutBtn = document.getElementById('logout-btn'),
        userEmailEl = document.getElementById('user-email'),
        userDetailsContainer = document.querySelector('.user-details');

    const tokenData = storageService.getTokens();
    if (!tokenData) {
        window.location.href = '/employee/login';
        return;
    }

    apiService.getUserMe()
        .then(response => {
            const userData = response.data;
            storageService.saveUserData(userData);
            if (userEmailEl) userEmailEl.textContent = userData.email;
            if (userDetailsContainer) {
                userDetailsContainer.innerHTML = '';
                const card = document.createElement('div');
                card.className = 'employee-card';
                addCardField(card, 'ФИО', userData.fullName || 'Не указано');
                addCardField(card, 'Email', userData.email);
                if (userData.gender) {
                    addCardField(card, 'Пол', formatGenderValue(userData.gender));
                }
                addCardField(card, 'Статус', userData.banned ? 'Заблокирован' : 'Активен');
                userDetailsContainer.appendChild(card);
            }
        })
        .catch(error => {
            if (userDetailsContainer) {
                userDetailsContainer.innerHTML = '<p class="error-message">Не удалось загрузить данные пользователя. Пожалуйста, попробуйте позже.</p>';
            }
        });

    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            try {
                await apiService.logout();
            } catch (error) {
                console.error('Logout error:', error);
            } finally {
                storageService.removeTokens();
                storageService.removeUserData();
                window.location.href = '/employee/login';
            }
        });
    }
};

const renderEmployeeCard = (userData, container) => {
    if (!container) return;
    container.innerHTML = '';
    const card = document.createElement('div');
    card.className = 'employee-card';
    addCardField(card, 'ФИО', userData.fullName || 'Не указано');
    addCardField(card, 'Email', userData.email);
    if (userData.gender) addCardField(card, 'Пол', formatGenderValue(userData.gender));
    addCardField(card, 'Статус', userData.banned ? 'Заблокирован' : 'Активен');
    container.appendChild(card);
};

const addCardField = (card, label, value) => {
    const element = document.createElement('p');
    element.innerHTML = `<strong>${label}:</strong> <span>${value}</span>`;
    card.appendChild(element);
};

const formatGenderValue = (gender) => {
    if (typeof gender === 'string') {
        return gender === 'MALE' ? 'Мужской' : gender === 'FEMALE' ? 'Женский' : gender;
    }
    if (gender && typeof gender === 'object') {
        if (gender.name) return gender.name === 'MALE' ? 'Мужской' : gender.name === 'FEMALE' ? 'Женский' : gender.name;
        if (gender.toString) {
            const genderStr = gender.toString();
            return genderStr === 'MALE' ? 'Мужской' : genderStr === 'FEMALE' ? 'Женский' : genderStr;
        }
    }
    return 'Не указано';
};

const checkAuth = () => {
    const currentPath = window.location.pathname,
        isLoginPage = currentPath === '/employee/login',
        isRegisterPage = currentPath === '/employee/register';

    if (!isLoginPage && !isRegisterPage) {
        const tokenData = storageService.getTokens();
        if (!tokenData) {
            window.location.href = '/employee/login';
            return false;
        }
        return true;
    } else if (storageService.getTokens()) {
        window.location.href = '/employee/me';
        return false;
    }
    return true;
};

document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;
    if (currentPath !== '/employee/login' && currentPath !== '/employee/register') {
        checkAuth();
        if (document.getElementById('logout-btn')) {
            initHomePage();
        }
    }
});
