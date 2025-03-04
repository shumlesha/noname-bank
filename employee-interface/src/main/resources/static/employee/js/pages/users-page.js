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

    loadUsers(0, USERS_PER_PAGE);
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
