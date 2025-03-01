const API_BASE_URL = '/api';
const AUTH_ENDPOINT = `${API_BASE_URL}/auth`;
const USERS_ENDPOINT = `${API_BASE_URL}/users`;
const TOKEN_STORAGE_KEY = 'auth_tokens';
const USER_STORAGE_KEY = 'user_data';

const API_URLS = {
    register: `${AUTH_ENDPOINT}/register`,
    login: `${AUTH_ENDPOINT}/login`,
    logout: `${AUTH_ENDPOINT}/logout`,
    me: `${USERS_ENDPOINT}/me`
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


        if (fetchOptions.skipAuth) {
            delete fetchOptions.skipAuth;
        }

        console.log('Выполнение запроса к:', url);
        console.log('Опции запроса:', JSON.stringify(fetchOptions));

        try {
            const response = await fetch(url, fetchOptions);
            console.log('Статус ответа:', response.status);
            
            let data;
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                const text = await response.text();
                console.log('Текстовый ответ:', text);
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


    register: async function(userData) {
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

        console.log('Отправляемые данные:', options.body);
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
        const options = {
            method: 'POST'
        };

        return this.fetch(API_URLS.logout, options);
    },


    getUserMe: async function() {
        const options = {
            method: 'GET'
        };

        return this.fetch(API_URLS.me, options);
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
        

        const genderRadio = document.querySelector('input[name="gender"]:checked');
        const gender = genderRadio ? genderRadio.value : null;
        
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

        console.log('Данные для регистрации:', userData);

        try {
            const response = await apiService.register(userData);
            console.log('Ответ сервера:', response);

            storageService.saveUserData({
                userId: response.data.userId,
                email: response.data.email
            });

            window.location.href = '/employee/login';
        } catch (error) {
            console.error('Ошибка при регистрации:', error);
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
            window.location.href = '/employee/me';
        } catch (error) {
            console.error('Ошибка при входе:', error);
            errorMessageEl.textContent = error.message || 'Неверный email или пароль';
            errorMessageEl.style.display = 'block';
        }
    });
}


function initHomePage() {
    const logoutBtn = document.getElementById('logout-btn');
    const userEmailEl = document.getElementById('user-email');
    const userDetailsContainer = document.querySelector('.user-details');

    const tokenData = storageService.getTokens();

    if (!tokenData) {
        window.location.href = '/employee/login';
        return;
    }


    apiService.getUserMe()
        .then(response => {
            const userData = response.data;
            

            console.log('User data received:', userData);
            console.log('Gender data:', userData.gender);
            

            storageService.saveUserData(userData);
            

            if (userEmailEl) userEmailEl.textContent = userData.email;
            

            if (userDetailsContainer) {
                userDetailsContainer.innerHTML = '';
                

                const card = document.createElement('div');
                card.className = 'employee-card';
                

                const nameElement = document.createElement('p');
                nameElement.innerHTML = `<strong>ФИО:</strong> <span>${userData.fullName || 'Не указано'}</span>`;
                card.appendChild(nameElement);
                

                const emailElement = document.createElement('p');
                emailElement.innerHTML = `<strong>Email:</strong> <span>${userData.email}</span>`;
                card.appendChild(emailElement);


                if (userData.gender) {
                    const genderText = formatGenderValue(userData.gender);
                    const genderElement = document.createElement('p');
                    genderElement.innerHTML = `<strong>Пол:</strong> <span>${genderText}</span>`;
                    card.appendChild(genderElement);
                }
                

                const statusElement = document.createElement('p');
                statusElement.innerHTML = `<strong>Статус:</strong> <span>${userData.banned ? 'Заблокирован' : 'Активен'}</span>`;
                card.appendChild(statusElement);
                
                userDetailsContainer.appendChild(card);
            }
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
            if (userDetailsContainer) {
                userDetailsContainer.innerHTML = '<p class="error-message">Не удалось загрузить данные пользователя. Пожалуйста, попробуйте позже.</p>';
            }
        });

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


function renderEmployeeCard(userData, container) {
    if (!container) return;
    
    container.innerHTML = '';

    const card = document.createElement('div');
    card.className = 'employee-card';

    addCardField(card, 'ФИО', userData.fullName || 'Не указано');

    addCardField(card, 'Email', userData.email);

    if (userData.gender) {
        const genderText = formatGenderValue(userData.gender);
        addCardField(card, 'Пол', genderText);
    }

    addCardField(card, 'Статус', userData.banned ? 'Заблокирован' : 'Активен');
    
    container.appendChild(card);
}


function addCardField(card, label, value) {
    const element = document.createElement('p');
    element.innerHTML = `<strong>${label}:</strong> <span>${value}</span>`;
    card.appendChild(element);
}


function formatGenderValue(gender) {
    if (typeof gender === 'string') {
        if (gender === 'MALE') return 'Мужской';
        if (gender === 'FEMALE') return 'Женский';
        return gender;
    } 
    

    if (gender && typeof gender === 'object') {
        if (gender.name) {
            if (gender.name === 'MALE') return 'Мужской';
            if (gender.name === 'FEMALE') return 'Женский';
            return gender.name;
        }
        

        if (gender.toString) {
            const genderStr = gender.toString();
            if (genderStr === 'MALE') return 'Мужской';
            if (genderStr === 'FEMALE') return 'Женский';
            return genderStr;
        }
    }
    
    return 'Не указано';
}


function showErrorMessage(container) {
    if (!container) return;
    
    container.innerHTML = '<p class="error-message">Не удалось загрузить данные пользователя. Пожалуйста, попробуйте позже.</p>';
}


async function handleLogout() {
    try {
        await apiService.logout();
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        storageService.removeTokens();
        storageService.removeUserData();
        window.location.href = '/employee/login';
    }
}


function checkAuth() {
    const currentPath = window.location.pathname;
    const isLoginPage = currentPath === '/employee/login';
    const isRegisterPage = currentPath === '/employee/register';
    
    if (!isLoginPage && !isRegisterPage) {
        const tokenData = storageService.getTokens();
        if (!tokenData) {
            window.location.href = '/employee/login';
        }
    } else if (storageService.getTokens()) {
        window.location.href = '/employee/me';
    }
}


document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;

    if (currentPath !== '/employee/login' && currentPath !== '/employee/register') {
        checkAuth();
    }
});