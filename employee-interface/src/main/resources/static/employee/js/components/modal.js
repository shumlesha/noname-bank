export class Modal {
    constructor(options = {}) {
        this.options = {
            title: options.title || 'Модальное окно',
            closable: options.closable !== undefined ? options.closable : true,
            content: options.content || '',
            width: options.width || '600px',
            onClose: options.onClose || function() {},
            footerButtons: options.footerButtons || []
        };

        this.modal = null;
        this.overlay = null;
        this.closeButton = null;
        this.isOpen = false;
        
        this.render();
        this.setupEventListeners();
    }

    render() {
        this.overlay = document.createElement('div');
        this.overlay.className = 'modal-overlay';
        
        const modalHTML = `
            <div class="modal-container" style="width: ${this.options.width}">
                <div class="modal-header">
                    <h3 class="modal-title">${this.options.title}</h3>
                    ${this.options.closable ? '<button class="modal-close">&times;</button>' : ''}
                </div>
                <div class="modal-body">
                    ${this.options.content}
                </div>
                ${this.renderFooter()}
            </div>
        `;
        
        this.overlay.innerHTML = modalHTML;
        document.body.appendChild(this.overlay);
        
        this.modal = this.overlay.querySelector('.modal-container');
        this.closeButton = this.overlay.querySelector('.modal-close');
    }

    renderFooter() {
        if (this.options.footerButtons.length === 0) {
            return '';
        }

        const buttons = this.options.footerButtons.map(btn => {
            return `<button class="modal-btn ${btn.class || ''}" data-btn-id="${btn.id}">${btn.text}</button>`;
        }).join('');

        return `<div class="modal-footer">${buttons}</div>`;
    }

    setupEventListeners() {
        if (this.closeButton) {
            this.closeButton.addEventListener('click', this.close.bind(this));
        }

        this.overlay.addEventListener('click', event => {
            if (event.target === this.overlay && this.options.closable) {
                this.close();
            }
        });

        const footerButtons = this.overlay.querySelectorAll('.modal-footer .modal-btn');
        footerButtons.forEach(button => {
            button.addEventListener('click', event => {
                const btnId = event.target.dataset.btnId;
                const btnConfig = this.options.footerButtons.find(btn => btn.id === btnId);
                if (btnConfig && typeof btnConfig.handler === 'function') {
                    btnConfig.handler();
                }
            });
        });
    }

    open() {
        if (!this.isOpen) {
            this.overlay.classList.add('animate-in');
            setTimeout(() => {
                this.overlay.classList.add('active');
            }, 10);
            document.body.style.overflow = 'hidden';
            this.isOpen = true;
        }
    }

    close() {
        if (this.isOpen) {
            this.overlay.classList.remove('active');
            setTimeout(() => {
                this.overlay.classList.remove('animate-in');
                if (typeof this.options.onClose === 'function') {
                    this.options.onClose();
                }
            }, 300);
            document.body.style.overflow = '';
            this.isOpen = false;
        }
    }

    setContent(content) {
        const modalBody = this.modal.querySelector('.modal-body');
        if (modalBody) {
            modalBody.innerHTML = content;
        }
    }

    setTitle(title) {
        const modalTitle = this.modal.querySelector('.modal-title');
        if (modalTitle) {
            modalTitle.textContent = title;
        }
    }

    destroy() {
        if (this.overlay) {
            this.overlay.remove();
        }
    }
}


export class TabbedModal extends Modal {
    constructor(options = {}) {
        super({
            ...options,
            content: ''
        });
        
        this.tabs = options.tabs || [];
        this.activeTabIndex = 0;
        
        this.renderTabs();
        this.setupTabEventListeners();
    }
    
    renderTabs() {
        if (this.tabs.length === 0) return;
        
        const modalBody = this.modal.querySelector('.modal-body');
        

        const tabsNav = document.createElement('div');
        tabsNav.className = 'modal-tabs';
        
        const tabsHTML = this.tabs.map((tab, index) => {
            return `<div class="modal-tab ${index === this.activeTabIndex ? 'active' : ''}" data-tab-index="${index}">${tab.title}</div>`;
        }).join('');
        
        tabsNav.innerHTML = tabsHTML;
        

        const tabContents = document.createElement('div');
        tabContents.className = 'modal-tab-contents';
        
        const contentsHTML = this.tabs.map((tab, index) => {
            return `<div class="modal-tab-content ${index === this.activeTabIndex ? 'active' : ''}" data-tab-content="${index}">${tab.content}</div>`;
        }).join('');
        
        tabContents.innerHTML = contentsHTML;
        

        modalBody.innerHTML = '';
        modalBody.appendChild(tabsNav);
        modalBody.appendChild(tabContents);
    }
    
    setupTabEventListeners() {
        const tabs = this.modal.querySelectorAll('.modal-tab');
        
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const index = parseInt(tab.dataset.tabIndex);
                this.activateTab(index);
            });
        });
    }
    
    activateTab(index) {
        if (index < 0 || index >= this.tabs.length) return;
        
        this.activeTabIndex = index;
        

        const tabs = this.modal.querySelectorAll('.modal-tab');
        tabs.forEach(tab => {
            tab.classList.remove('active');
        });
        
        const activeTab = this.modal.querySelector(`.modal-tab[data-tab-index="${index}"]`);
        if (activeTab) activeTab.classList.add('active');
        

        const contents = this.modal.querySelectorAll('.modal-tab-content');
        contents.forEach(content => {
            content.classList.remove('active');
        });
        
        const activeContent = this.modal.querySelector(`.modal-tab-content[data-tab-content="${index}"]`);
        if (activeContent) activeContent.classList.add('active');
    }
    
    updateTabContent(index, content) {
        if (index < 0 || index >= this.tabs.length) return;
        
        this.tabs[index].content = content;
        
        const tabContent = this.modal.querySelector(`.modal-tab-content[data-tab-content="${index}"]`);
        if (tabContent) {
            tabContent.innerHTML = content;
        }
    }
}
