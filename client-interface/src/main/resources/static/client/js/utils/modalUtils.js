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

export function showPushNotification({ title, body, icon, ttl = 15 }) {
    let root = document.getElementById('toast-root');
    if (!root) {
        root = Object.assign(document.createElement('div'), { id: 'toast-root' });
        Object.assign(root.style, {
            position: 'fixed', right: '16px', bottom: '16px',
            display: 'flex', flexDirection: 'column', gap: '12px',
            zIndex: 9999, pointerEvents: 'none'
        });
        document.body.appendChild(root);
    }

    const toast = document.createElement('div');
    const iconHtml = icon ? `<div class="toast-icon"><img src="${icon}" alt=""></div>` : '';

    toast.innerHTML = `
        <div class="toast-content">
            ${iconHtml}
            <div class="toast-text">
                <strong>${title}</strong>
                <p>${body.replaceAll('\n','<br>')}</p>
            </div>
        </div>
    `;

    Object.assign(toast.style, {
        maxWidth: '360px', padding: '14px 18px',
        background: 'linear-gradient(135deg, #323232, #222)',
        color: '#fff', border: '1px solid rgba(255,255,255,0.1)',
        borderRadius: '12px', boxShadow: '0 4px 16px rgba(0,0,0,0.2)',
        fontSize: '14px', lineHeight: '20px',
        opacity: '0', transform: 'translateY(20px) scale(0.95)',
        transition: 'all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275)',
        pointerEvents: 'auto'
    });

    const toastContent = toast.querySelector('.toast-content');
    Object.assign(toastContent.style, {
        display: 'flex',
        alignItems: 'flex-start',
        gap: '12px'
    });

    if (icon) {
        const iconDiv = toast.querySelector('.toast-icon');
        Object.assign(iconDiv.style, {
            flexShrink: '0',
            width: '24px',
            height: '24px',
            marginTop: '2px'
        });

        const iconImg = iconDiv.querySelector('img');
        Object.assign(iconImg.style, {
            width: '100%',
            height: '100%',
            objectFit: 'contain'
        });
    }

    const textDiv = toast.querySelector('.toast-text');
    Object.assign(textDiv.style, {
        flex: '1'
    });

    const titleElement = textDiv.querySelector('strong');
    Object.assign(titleElement.style, {
        display: 'block',
        fontSize: '15px',
        fontWeight: '600',
        marginBottom: '4px'
    });

    const bodyElement = textDiv.querySelector('p');
    Object.assign(bodyElement.style, {
        margin: '0',
        color: 'rgba(255,255,255,0.9)'
    });

    root.appendChild(toast);

    toast.addEventListener('mouseenter', () => {
        toast.style.transform = 'translateY(0) scale(1.02)';
        toast.style.boxShadow = '0 6px 20px rgba(0,0,0,0.25)';
    });

    toast.addEventListener('mouseleave', () => {
        toast.style.transform = 'translateY(0) scale(1)';
        toast.style.boxShadow = '0 4px 16px rgba(0,0,0,0.2)';
    });

    requestAnimationFrame(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0) scale(1)';
    });

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px) scale(0.95)';
        toast.addEventListener('transitionend', () => root.removeChild(toast), { once: true });
    }, ttl * 1000);
}