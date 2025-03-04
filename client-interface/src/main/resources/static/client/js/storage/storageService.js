const TOKEN_STORAGE_KEY = 'auth_tokens_client';
const USER_STORAGE_KEY = 'user_data_client';

export const storageService = {
    saveTokens(tokenData) {
        localStorage.setItem(TOKEN_STORAGE_KEY, JSON.stringify(tokenData));
    },

    getTokens() {
        const tokensStr = localStorage.getItem(TOKEN_STORAGE_KEY);
        if (!tokensStr) return null;

        try {
            return JSON.parse(tokensStr);
        } catch (error) {
            console.error("Ошибка разбора токенов:", error);
            localStorage.removeItem(TOKEN_STORAGE_KEY);
            return null;
        }
    }
    ,

    removeTokens() {
        localStorage.removeItem(TOKEN_STORAGE_KEY);
    },

    saveUserData(userData) {
        localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(userData));
    },

    getUserData() {
        const userDataStr = localStorage.getItem(USER_STORAGE_KEY);
        return userDataStr ? JSON.parse(userDataStr) : null;
    },

    removeUserData() {
        localStorage.removeItem(USER_STORAGE_KEY);
    }
};
