export function showMessage(message, title = 'Сообщение', type = 'info') {
    const modal = document.getElementById('message-modal');
    const titleElement = document.getElementById('message-modal-title');
    const textElement = document.getElementById('message-modal-text');
    const okButton = document.getElementById('message-modal-ok');

    titleElement.textContent = title;
    textElement.textContent = message;

    textElement.className = '';
    okButton.className = 'btn btn-primary';
    
    if (type === 'error') {
        textElement.classList.add('error-message-text');
        titleElement.style.color = '#e74c3c';
        okButton.classList.remove('btn-primary');
        okButton.classList.add('btn-danger');
    } else if (type === 'success') {
        textElement.classList.add('success-message-text');
        titleElement.style.color = '#27ae60';
    } else if (type === 'warning') {
        textElement.classList.add('warning-message-text');
        titleElement.style.color = '#f39c12';
    } else {
        titleElement.style.color = '#27ae60';
    }

    modal.classList.add('show');

    const closeBtn = document.getElementById('close-message-modal-btn');
    
    const closeModal = () => {
        modal.classList.remove('show');
        closeBtn.removeEventListener('click', closeModal);
        okButton.removeEventListener('click', closeModal);
    };
    
    closeBtn.addEventListener('click', closeModal);
    okButton.addEventListener('click', closeModal);

    return new Promise(resolve => {
        okButton.addEventListener('click', () => {
            closeModal();
            resolve(true);
        });
    });
}

export function showError(message) {
    return showMessage(message, 'Ошибка', 'error');
}

export function showSuccess(message) {
    return showMessage(message, 'Успешно', 'success');
}
export function showConfirm(message, title = 'Подтверждение') {
    return showMessage(message, title, 'warning');
}
