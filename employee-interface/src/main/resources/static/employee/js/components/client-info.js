export const renderClientInfo = (client, container) => {
    if (!client || typeof client !== 'object') {
        container.innerHTML = '<p class="error-message">Некорректные данные клиента</p>';
        return;
    }

    const infoContainer = document.createElement('div');
    infoContainer.className = 'client-info';

    const idItem = document.createElement('p');
    idItem.innerHTML = `<strong>ID клиента:</strong> ${client.id || 'Нет данных'}`;
    infoContainer.appendChild(idItem);

    const nameItem = document.createElement('p');
    nameItem.innerHTML = `<strong>Полное имя:</strong> ${client.fullName || 'Нет данных'}`;
    infoContainer.appendChild(nameItem);

    const emailItem = document.createElement('p');
    emailItem.innerHTML = `<strong>Email:</strong> ${client.email || 'Нет данных'}`;
    infoContainer.appendChild(emailItem);

    if (client.gender) {
        const genderItem = document.createElement('p');
        genderItem.innerHTML = `<strong>Пол:</strong> ${client.gender === 'MALE' ? 'Мужской' : 'Женский'}`;
        infoContainer.appendChild(genderItem);
    }

    const statusItem = document.createElement('p');
    statusItem.innerHTML = `<strong>Статус:</strong> <span class="user-status ${client.banned ? 'banned' : 'active'}">${client.banned ? 'Заблокирован' : 'Активен'}</span>`;
    infoContainer.appendChild(statusItem);

    const backButton = document.createElement('button');
    backButton.className = 'btn back-button';
    backButton.textContent = 'Вернуться назад';
    backButton.addEventListener('click', () => {
        window.history.back();
    });

    container.innerHTML = '';
    container.appendChild(backButton);
    container.appendChild(infoContainer);
};
