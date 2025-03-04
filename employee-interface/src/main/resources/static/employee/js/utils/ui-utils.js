export const showMessage = (message, type = 'info') => {
    const messagesContainer = document.getElementById('messages-container');
    if (!messagesContainer) return;

    const messageDiv = document.createElement('div');
    messageDiv.className = 'error-message-container';

    const p = document.createElement('p');
    p.className = `${type}-message`;
    p.textContent = message;

    messageDiv.appendChild(p);
    messagesContainer.innerHTML = '';
    messagesContainer.appendChild(messageDiv);

    if (type === 'info' || type === 'success') {
        setTimeout(() => {
            messageDiv.style.opacity = '0';
            setTimeout(() => {
                if (messagesContainer.contains(messageDiv)) {
                    messagesContainer.removeChild(messageDiv);
                }
            }, 500);
        }, 5000);
    }
};


export const clearMessages = () => {
    const messagesContainer = document.getElementById('messages-container');
    if (messagesContainer) messagesContainer.innerHTML = '';
};


export const showElement = (element, displayStyle = 'block') => {
    if (element) element.style.display = displayStyle;
};


export const hideElement = (element) => {
    if (element) element.style.display = 'none';
};


export const createButton = (text, disabled, onClick) => {
    const btn = document.createElement('button');
    btn.className = 'btn btn-pagination';
    btn.textContent = text;
    btn.disabled = disabled;
    btn.addEventListener('click', onClick);
    return btn;
};


export const addCardField = (card, label, value) => {
    const element = document.createElement('p');
    element.innerHTML = `<strong>${label}:</strong> <span>${value}</span>`;
    card.appendChild(element);
};