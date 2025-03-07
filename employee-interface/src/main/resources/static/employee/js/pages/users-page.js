import API_CONFIG from '../config/api-config.js';
import apiService from '../services/api-service.js';
import { checkAuth } from '../auth.js';
import { renderUsersList, renderUserDetails } from '../components/users-list.js';
import { renderPagination } from '../components/pagination.js';
import { showMessage, clearMessages, showElement, hideElement } from '../utils/ui-utils.js';

const USERS_PER_PAGE = API_CONFIG.PAGINATION.USERS_PER_PAGE;

let currentPage = 0;
let totalUsersCount = 0;


export const initUsersPage = () => {
    if (window.usersPageInitialized) return;
    window.usersPageInitialized = true;

    if (!checkAuth()) {
        console.error('Пользователь не авторизован');
        return;
    }

    clearMessages();

    initCreateUserModal();
    
    loadUsers(0, USERS_PER_PAGE);
};

const initCreateUserModal = () => {
    const createUserBtn = document.getElementById('create-user-btn');
    const createUserModal = document.getElementById('create-user-modal');
    const closeModalBtn = document.getElementById('close-create-user-modal-btn');
    const cancelBtn = document.getElementById('cancel-create-user-btn');
    const submitBtn = document.getElementById('submit-create-user-btn');
    
    if (!createUserBtn || !createUserModal) return;
    

    createUserBtn.addEventListener('click', () => {
        document.getElementById('create-user-form').reset();
        document.getElementById('create-user-error').style.display = 'none';
        createUserModal.style.display = 'flex';
    });
    

    const closeModal = () => {
        createUserModal.style.display = 'none';
    };
    
    closeModalBtn.addEventListener('click', closeModal);
    cancelBtn.addEventListener('click', closeModal);

    createUserModal.addEventListener('click', (e) => {
        if (e.target === createUserModal) {
            closeModal();
        }
    });
    

    submitBtn.addEventListener('click', createUser);
};


const createUser = async () => {
    const errorMessageEl = document.getElementById('create-user-error');
    errorMessageEl.style.display = 'none';
    
    const email = document.getElementById('create-email').value;
    const fullName = document.getElementById('create-fullName').value;
    const genderRadio = document.querySelector('input[name="create-gender"]:checked');
    const gender = genderRadio ? genderRadio.value : null;
    const password = document.getElementById('create-password').value;
    const confirmPassword = document.getElementById('create-confirmPassword').value;
    

    if (!email || !fullName || !gender || !password || !confirmPassword) {
        errorMessageEl.textContent = 'Пожалуйста, заполните все обязательные поля';
        errorMessageEl.style.display = 'block';
        return;
    }
    
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
    
    const roleCheckboxes = document.querySelectorAll('input[name="create-roles"]:checked');
    const roles = Array.from(roleCheckboxes).map(cb => cb.value);
    
    if (roles.length === 0) {
        errorMessageEl.textContent = 'Выберите хотя бы одну роль';
        errorMessageEl.style.display = 'block';
        return;
    }
    
    const userData = { email, fullName, gender, roles, password };
    
    try {
        const submitBtn = document.getElementById('submit-create-user-btn');
        submitBtn.disabled = true;
        submitBtn.textContent = 'Создание...';

        await apiService.register(userData);

        document.getElementById('create-user-modal').style.display = 'none';

        showMessage('Пользователь успешно создан', 'success');

        loadUsers(currentPage, USERS_PER_PAGE);
    } catch (error) {
        errorMessageEl.textContent = error.message || 'Произошла ошибка при создании пользователя';
        errorMessageEl.style.display = 'block';
    } finally {
        const submitBtn = document.getElementById('submit-create-user-btn');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Создать';
    }
};

const loadUsers = async (page, pageSize) => {
    const usersContainer = document.getElementById('users-container');
    const loadingIndicator = document.getElementById('users-loading');
    const paginationContainer = document.getElementById('pagination-container');
    const userDetailsContainer = document.getElementById('user-details-container');

    if (!usersContainer) return;

    currentPage = page;

    showElement(loadingIndicator);
    showElement(usersContainer);
    showElement(paginationContainer, 'flex');
    hideElement(userDetailsContainer);

    try {
        const response = await apiService.getAllUsers(pageSize, page);
        hideElement(loadingIndicator);

        if (response && response.data) {
            let users = [];
            let totalElements = 0;

            if (response.data.content && Array.isArray(response.data.content)) {
                users = response.data.content;
                totalElements = response.data.totalElements || users.length;
                totalUsersCount = totalElements;
            } else if (Array.isArray(response.data)) {
                users = response.data;
                totalUsersCount = users.length;
            } else {
                console.error('Неожиданный формат данных:', response.data);
                showMessage('Неожиданный формат данных от сервера', 'error');
                usersContainer.innerHTML = '';
                return;
            }

            renderUsersList(users, usersContainer,
                (user) => {
                    showUserDetails(user);
                },
                (user) => {
                    showBanUserForm(user);
                }
            );

            const totalPages = Math.ceil(totalUsersCount / pageSize);
            renderPagination(paginationContainer, page, totalPages, (newPage) => {
                loadUsers(newPage, USERS_PER_PAGE);
            });
        } else {
            showMessage('Не удалось загрузить список пользователей', 'error');
            usersContainer.innerHTML = '';
        }
    } catch (error) {
        console.error('Ошибка при загрузке пользователей:', error);
        hideElement(loadingIndicator);
        showMessage('Произошла ошибка при загрузке пользователей', 'error');
        usersContainer.innerHTML = '';
    }
};

const showUserDetails = (user) => {
    const usersContainer = document.getElementById('users-container');
    const paginationContainer = document.getElementById('pagination-container');
    const userDetailsContainer = document.getElementById('user-details-container');

    hideElement(usersContainer);
    hideElement(paginationContainer);
    showElement(userDetailsContainer);

    renderUserDetails(user, userDetailsContainer, (user, reason) => {
        banUser(user.id, reason);
    });

    const backButton = document.createElement('button');
    backButton.className = 'btn back-button';
    backButton.textContent = 'Вернуться к списку пользователей';
    backButton.addEventListener('click', () => {
        hideElement(userDetailsContainer);
        showElement(usersContainer);
        showElement(paginationContainer, 'flex');
    });

    userDetailsContainer.insertBefore(backButton, userDetailsContainer.firstChild);
};

const showBanUserForm = (user) => {
    showUserDetails(user);
};

const banUser = async (userId, reason) => {
    clearMessages();
    
    try {
        const response = await apiService.banUser(userId, reason);
        
        if (response && response.data) {
            showMessage(`Пользователь успешно заблокирован: ${response.data.email}`, 'success');

            setTimeout(() => {
                loadUsers(currentPage, USERS_PER_PAGE);

                const userDetailsContainer = document.getElementById('user-details-container');
                hideElement(userDetailsContainer);

                const usersContainer = document.getElementById('users-container');
                const paginationContainer = document.getElementById('pagination-container');
                showElement(usersContainer);
                showElement(paginationContainer, 'flex');
            }, 2000);
        } else {
            showMessage('Не удалось заблокировать пользователя', 'error');
        }
    } catch (error) {
        console.error('Ошибка при блокировке пользователя:', error);
        showMessage(`Ошибка при блокировке пользователя: ${error.message}`, 'error');
    }
};
