import {config} from '../config/config.js';

class StorageService {

    saveTokens(tokenData) {
        localStorage.setItem(config.storage.tokens, JSON.stringify(tokenData));
    }

    getTokens() {
        const tokensStr = localStorage.getItem(config.storage.tokens);
        if (!tokensStr) return null;

        try {
            return JSON.parse(tokensStr);
        } catch (error) {
            console.error("Ошибка разбора токенов:", error);
            localStorage.removeItem(config.storage.tokens);
            return null;
        }
    }

    removeTokens() {
        localStorage.removeItem(config.storage.tokens);
    }

    saveUserData(userData) {
        localStorage.setItem(config.storage.userData, JSON.stringify(userData));
    }

    getUserData() {
        const userDataStr = localStorage.getItem(config.storage.userData);
        return userDataStr ? JSON.parse(userDataStr) : null;
    }

    removeUserData() {
        localStorage.removeItem(config.storage.userData);
    }
}

export const storageService = new StorageService();
