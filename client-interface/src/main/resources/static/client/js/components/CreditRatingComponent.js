import {DomUtils} from '../utils/domUtils.js';
import {creditService} from '../core/creditService.js';
import {showError} from '../utils/modalUtils.js';

export class CreditRatingComponent {
    constructor(parentController) {
        this.parentController = parentController;
    }

    async loadCreditRating(userId) {
        try {
            const response = await creditService.getCreditRating(userId);
            const ratingValue = DomUtils.find('#credit-rating-value');
            if (ratingValue) {
                ratingValue.textContent = response.data.rating;

                const rating = parseFloat(response.data.rating);
                if (rating >= 4.5) {
                    ratingValue.style.backgroundColor = '#28a745';
                } else if (rating >= 3.5) {
                    ratingValue.style.backgroundColor = '#007bff';
                } else if (rating >= 2.5) {
                    ratingValue.style.backgroundColor = '#ffc107';
                } else {
                    ratingValue.style.backgroundColor = '#dc3545';
                }
            }
        } catch (error) {
            console.error("Ошибка при загрузке рейтинга:", error);
            showError("Ошибка при загрузке кредитного рейтинга");
        }
    }
} 