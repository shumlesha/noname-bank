import {config} from '../config/config.js';

class StorageService {
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
