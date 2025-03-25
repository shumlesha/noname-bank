import {formatCurrency} from '../utils/format-utils.js';

export const renderCreditRating = (ratingData, container) => {
    if (!ratingData) {
        container.innerHTML = '<div class="error-message-container"><p class="info-message">Данные о кредитном рейтинге не найдены</p></div>';
        return;
    }

    const ratingValue = ratingData.rating || 0;
    const ratingClass = getCreditRatingClass(ratingValue);
    const ratingDescription = getCreditRatingDescription(ratingValue);

    const ratingCard = document.createElement('div');
    ratingCard.className = 'credit-rating-card';

    ratingCard.innerHTML = `
        <h3>Кредитный рейтинг</h3>
        <div class="credit-rating-value ${ratingClass}">
            <span class="rating-number">${ratingValue.toFixed(1)}</span>
            <div class="rating-bar">
                <div class="rating-bar-fill ${ratingClass}" style="width: ${(ratingValue / 10) * 100}%"></div>
            </div>
        </div>
        <div class="credit-rating-description">
            <p>${ratingDescription}</p>
        </div>
        <div class="credit-rating-info">
            <p>Кредитный рейтинг отражает платежеспособность клиента и его кредитную историю.</p>
            <p>Шкала рейтинга: от 1.0 (низкий) до 10.0 (высокий).</p>
        </div>
    `;

    container.innerHTML = '';
    container.appendChild(ratingCard);
};

const getCreditRatingClass = (rating) => {
    if (rating >= 8) return 'excellent';
    if (rating >= 6) return 'good';
    if (rating >= 4) return 'average';
    if (rating >= 2) return 'poor';
    return 'very-poor';
};

const getCreditRatingDescription = (rating) => {
    if (rating >= 8) return 'Отличный кредитный рейтинг. Клиент имеет высокую платежеспособность и надежную кредитную историю.';
    if (rating >= 6) return 'Хороший кредитный рейтинг. Клиент имеет стабильную платежеспособность и положительную кредитную историю.';
    if (rating >= 4) return 'Средний кредитный рейтинг. Клиент имеет удовлетворительную платежеспособность и кредитную историю.';
    if (rating >= 2) return 'Низкий кредитный рейтинг. Клиент имеет проблемы с платежеспособностью или негативную кредитную историю.';
    return 'Очень низкий кредитный рейтинг. Клиент имеет серьезные проблемы с платежеспособностью и негативную кредитную историю.';
};
