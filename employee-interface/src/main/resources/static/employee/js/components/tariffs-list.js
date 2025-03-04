export const renderTariffsList = (tariffs, container, onEditTariff, onDeleteTariff) => {
    if (!tariffs || tariffs.length === 0) {
        container.innerHTML = '<div class="error-message-container"><p class="info-message">Тарифы не найдены</p></div>';
        return;
    }

    const table = document.createElement('table');
    table.className = 'tariffs-table';

    table.innerHTML = `
    <thead>
        <tr>
            <th>ID тарифа</th>
            <th>Название</th>
            <th>Процентная ставка</th>
            <th>Действия</th>
        </tr>
    </thead>
    <tbody>
        ${tariffs.map(tariff => {
            return `
            <tr>
                <td>${tariff.id}</td>
                <td>${tariff.name}</td>
                <td>${tariff.interestRate}%</td>
                <td>
                    <div class="tariff-actions">
                        <button class="btn btn-edit" data-tariff-id="${tariff.id}">Редактировать</button>
                        <button class="btn btn-delete" data-tariff-id="${tariff.id}" data-tariff-name="${tariff.name}">Удалить</button>
                    </div>
                </td>
            </tr>`;
        }).join('')}
    </tbody>`;

    container.innerHTML = '';
    container.appendChild(table);


    container.querySelectorAll('.btn-edit').forEach(button => {
        button.addEventListener('click', () => {
            const tariffId = button.getAttribute('data-tariff-id');
            if (onEditTariff && typeof onEditTariff === 'function') {
                onEditTariff(tariffId);
            }
        });
    });

    container.querySelectorAll('.btn-delete').forEach(button => {
        button.addEventListener('click', () => {
            const tariffId = button.getAttribute('data-tariff-id');
            const tariffName = button.getAttribute('data-tariff-name');
            if (onDeleteTariff && typeof onDeleteTariff === 'function') {
                onDeleteTariff(tariffId, tariffName);
            }
        });
    });
};
