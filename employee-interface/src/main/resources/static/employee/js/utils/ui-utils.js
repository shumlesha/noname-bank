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

export const clearMessages = () => {
    const messagesContainer = document.getElementById('messages-container');
    if (messagesContainer) messagesContainer.innerHTML = '';
};


export const showElement = (element, displayStyle = 'block') => {
    const el = typeof element === 'string' ? document.getElementById(element) : element;
    if (el) el.style.display = displayStyle;
};


export const hideElement = (element) => {
    const el = typeof element === 'string' ? document.getElementById(element) : element;
    if (el) el.style.display = 'none';
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