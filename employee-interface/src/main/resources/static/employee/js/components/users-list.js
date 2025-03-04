export const renderUsersList = (users, container, onViewDetails, onBanUser) => {
    if (!users || users.length === 0) {
        container.innerHTML = '<p class="no-data">Пользователи не найдены</p>';
        return;
    }

    const table = document.createElement('table');
    table.className = 'users-list';

    table.innerHTML = `
    <thead>
        <tr>
            <th>Имя</th>
            <th>Email</th>
            <th>Роли</th>
            <th>Статус</th>
            <th>Действия</th>
        </tr>
    </thead>
    <tbody>
        ${users.map(user => {
        let rolesHTML = '';
        if (user.roles && user.roles.length > 0) {
            rolesHTML = user.roles.map(role =>
                `<span class="user-role ${role.name.toLowerCase()}">${role.name}</span>`
            ).join('');
        } else {
            rolesHTML = 'Нет ролей';
        }

        const statusHTML = `<span class="user-status ${user.banned ? 'banned' : 'active'}">${user.banned ? 'Заблокирован' : 'Активен'}</span>`;

        let actionsHTML = `<button class="btn btn-view" data-user-id="${user.id}">Просмотр</button>`;
        if (user.banned) {
            actionsHTML += `<button class="btn btn-unban" data-user-id="${user.id}" disabled>Разблокировать</button>`;
        } else {
            actionsHTML += `<button class="btn btn-ban" data-user-id="${user.id}">Заблокировать</button>`;
        }

        return `
            <tr>
                <td>${user.fullName}</td>
                <td>${user.email}</td>
                <td><div class="user-roles">${rolesHTML}</div></td>
                <td>${statusHTML}</td>
                <td><div class="user-actions">${actionsHTML}</div></td>
            </tr>`;
    }).join('')}
    </tbody>`;

    container.innerHTML = '';
    container.appendChild(table);

    table.querySelectorAll('.btn-view').forEach(button => {
        button.addEventListener('click', () => {
            const userId = button.getAttribute('data-user-id');
            const user = users.find(u => u.id === userId);
            if (user && onViewDetails) {
                onViewDetails(user);
            }
        });
    });

    table.querySelectorAll('.btn-ban').forEach(button => {
        button.addEventListener('click', () => {
            const userId = button.getAttribute('data-user-id');
            const user = users.find(u => u.id === userId);
            if (user && onBanUser) {
                onBanUser(user);
            }
        });
    });
};


export const renderUserDetails = (user, container, onBanUser) => {
    container.innerHTML = '';

    const detailsContainer = document.createElement('div');
    detailsContainer.className = 'user-details';

    const header = document.createElement('div');
    header.className = 'user-details-header';
    header.innerHTML = `<h3>Информация о пользователе: ${user.fullName}</h3>`;
    detailsContainer.appendChild(header);

    const content = document.createElement('div');
    content.className = 'user-details-content';

    const idItem = document.createElement('div');
    idItem.className = 'user-detail-item';
    idItem.innerHTML = `
        <span class="user-detail-label">ID пользователя</span>
        <span class="user-detail-value">${user.id}</span>
    `;
    content.appendChild(idItem);

    const nameItem = document.createElement('div');
    nameItem.className = 'user-detail-item';
    nameItem.innerHTML = `
        <span class="user-detail-label">Полное имя</span>
        <span class="user-detail-value">${user.fullName}</span>
    `;
    content.appendChild(nameItem);

    const emailItem = document.createElement('div');
    emailItem.className = 'user-detail-item';
    emailItem.innerHTML = `
        <span class="user-detail-label">Email</span>
        <span class="user-detail-value">${user.email}</span>
    `;
    content.appendChild(emailItem);

    if (user.gender) {
        const genderItem = document.createElement('div');
        genderItem.className = 'user-detail-item';
        genderItem.innerHTML = `
            <span class="user-detail-label">Пол</span>
            <span class="user-detail-value">${user.gender === 'MALE' ? 'Мужской' : 'Женский'}</span>
        `;
        content.appendChild(genderItem);
    }

    const rolesItem = document.createElement('div');
    rolesItem.className = 'user-detail-item';

    const rolesLabel = document.createElement('span');
    rolesLabel.className = 'user-detail-label';
    rolesLabel.textContent = 'Роли';
    rolesItem.appendChild(rolesLabel);

    const rolesValue = document.createElement('div');
    rolesValue.className = 'user-roles';

    if (user.roles && user.roles.length > 0) {
        user.roles.forEach(role => {
            const roleSpan = document.createElement('span');
            roleSpan.className = `user-role ${role.name.toLowerCase()}`;
            roleSpan.textContent = role.name;
            rolesValue.appendChild(roleSpan);
        });
    } else {
        rolesValue.textContent = 'Нет ролей';
    }

    rolesItem.appendChild(rolesValue);
    content.appendChild(rolesItem);

    const statusItem = document.createElement('div');
    statusItem.className = 'user-detail-item';
    statusItem.innerHTML = `
        <span class="user-detail-label">Статус</span>
        <span class="user-status ${user.banned ? 'banned' : 'active'}">${user.banned ? 'Заблокирован' : 'Активен'}</span>
    `;
    content.appendChild(statusItem);

    detailsContainer.appendChild(content);

    if (!user.banned) {
        const banForm = document.createElement('div');
        banForm.className = 'ban-form';
        banForm.innerHTML = `
            <h3>Заблокировать пользователя</h3>
            <div class="form-group">
                <label for="ban-reason">Причина блокировки (необязательно)</label>
                <textarea id="ban-reason" placeholder="Укажите причину блокировки пользователя (при необходимости)"></textarea>
            </div>
            <button id="ban-submit-btn" class="btn btn-ban">Заблокировать пользователя</button>
        `;

        detailsContainer.appendChild(banForm);

        setTimeout(() => {
            const banSubmitBtn = document.getElementById('ban-submit-btn');
            const banReasonInput = document.getElementById('ban-reason');

            if (banSubmitBtn && banReasonInput) {
                banSubmitBtn.addEventListener('click', () => {
                    const reason = banReasonInput.value.trim();
                    onBanUser(user, reason);
                });
            }
        }, 0);
    }

    container.appendChild(detailsContainer);
};
