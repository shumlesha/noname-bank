export class DomUtils {

    static find(selector) {
        return document.querySelector(selector);
    }

    static findAll(selector) {
        return document.querySelectorAll(selector);
    }

    static on(element, event, handler) {
        const el = typeof element === 'string' ? this.find(element) : element;
        if (el) {
            el.addEventListener(event, handler);
        }
    }

    static showModal(id) {
        const modal = this.find(`#${id}`);
        if (modal) {
            modal.style.display = "block";
            modal.classList.add("show");
        }
    }

    static hideModal(id) {
        const modal = this.find(`#${id}`);
        if (modal) {
            modal.style.display = "none";
            modal.classList.remove("show");
        }
    }
}
