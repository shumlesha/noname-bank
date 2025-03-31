import {DomUtils} from '../utils/domUtils.js';

export class PaginationComponent {
    constructor(containerId, pageSize, renderCallback) {
        this.containerId = containerId;
        this.pageSize = pageSize;
        this.currentPage = 1;
        this.totalItems = 0;
        this.totalPages = 0;
        this.renderCallback = renderCallback;
    }

    setTotalItems(totalItems) {
        this.totalItems = totalItems;
        this.totalPages = Math.ceil(totalItems / this.pageSize);
        this.currentPage = this.currentPage > this.totalPages ? 1 : this.currentPage;
        this.render();
    }

    setPageSize(pageSize) {
        this.pageSize = pageSize;
        this.totalPages = Math.ceil(this.totalItems / this.pageSize);
        this.currentPage = this.currentPage > this.totalPages ? 1 : this.currentPage;
        this.render();
        this.onPageChange();
    }

    getCurrentPage() {
        return this.currentPage;
    }

    getPageSize() {
        return this.pageSize;
    }

    getPaginatedItems(items) {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        return items.slice(startIndex, startIndex + this.pageSize);
    }

    goToPage(page) {
        if (page < 1 || page > this.totalPages || page === this.currentPage) {
            return;
        }
        this.currentPage = page;
        this.render();
        this.onPageChange();
    }

    nextPage() {
        if (this.currentPage < this.totalPages) {
            this.currentPage++;
            this.render();
            this.onPageChange();
        }
    }

    prevPage() {
        if (this.currentPage > 1) {
            this.currentPage--;
            this.render();
            this.onPageChange();
        }
    }

    onPageChange() {
        if (this.renderCallback) {
            this.renderCallback(this.currentPage, this.pageSize);
        }
    }

    render() {
        const container = DomUtils.find(`#${this.containerId}`);
        if (!container) return;

        if (this.totalPages <= 1) {
            container.innerHTML = '';
            return;
        }

        let html = `
            <div class="pagination">
                <button class="pagination-btn prev-btn ${this.currentPage === 1 ? 'disabled' : ''}" 
                    ${this.currentPage === 1 ? 'disabled' : ''}>
                    &laquo; Предыдущая
                </button>
                <div class="pagination-pages">
                    <span>Страница ${this.currentPage} из ${this.totalPages}</span>
                </div>
                <button class="pagination-btn next-btn ${this.currentPage === this.totalPages ? 'disabled' : ''}" 
                    ${this.currentPage === this.totalPages ? 'disabled' : ''}>
                    Следующая &raquo;
                </button>
            </div>
        `;

        container.innerHTML = html;

        const prevBtn = container.querySelector('.prev-btn');
        const nextBtn = container.querySelector('.next-btn');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => this.prevPage());
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => this.nextPage());
        }
    }
} 