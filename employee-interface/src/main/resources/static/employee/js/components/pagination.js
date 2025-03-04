import { showElement, hideElement, createButton } from '../utils/ui-utils.js';


export const renderPagination = (container, currentPage, totalPages, onPageChange) => {
    container.innerHTML = '';

    if (totalPages <= 1) {
        hideElement(container);
        return;
    }

    showElement(container, 'flex');


    const prevButton = createButton('Назад', currentPage === 0, () => {
        if (currentPage > 0) onPageChange(currentPage - 1);
    });
    container.appendChild(prevButton);


    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages - 1, currentPage + 2);
    const pagesShown = endPage - startPage + 1;

    if (pagesShown < 5) {
        if (startPage === 0) {
            endPage = Math.min(totalPages - 1, endPage + (5 - pagesShown));
        } else if (endPage === totalPages - 1) {
            startPage = Math.max(0, startPage - (5 - pagesShown));
        }
    }


    if (startPage > 0) {
        const firstPageBtn = createButton(1, false, () => onPageChange(0));
        container.appendChild(firstPageBtn);

        if (startPage > 1) {
            const ellipsis = document.createElement('span');
            ellipsis.className = 'pagination-ellipsis';
            ellipsis.textContent = '...';
            container.appendChild(ellipsis);
        }
    }


    for (let i = startPage; i <= endPage; i++) {
        const btn = createButton(i + 1, i === currentPage, () => onPageChange(i));
        if (i === currentPage) btn.classList.add('active');
        container.appendChild(btn);
    }


    if (endPage < totalPages - 1) {
        if (endPage < totalPages - 2) {
            const ellipsis = document.createElement('span');
            ellipsis.className = 'pagination-ellipsis';
            ellipsis.textContent = '...';
            container.appendChild(ellipsis);
        }

        const lastPageBtn = createButton(totalPages, false, () => onPageChange(totalPages - 1));
        container.appendChild(lastPageBtn);
    }

    const nextButton = createButton('Вперед', currentPage === totalPages - 1, () => {
        if (currentPage < totalPages - 1) onPageChange(currentPage + 1);
    });
    container.appendChild(nextButton);
};