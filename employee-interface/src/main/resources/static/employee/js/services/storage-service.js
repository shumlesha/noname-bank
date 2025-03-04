import API_CONFIG from '../config/api-config.js';

const storageService = {
    saveTokens: (tokenData) => {
        if (!tokenData) {
            console.error('Token data is null or undefined');
            return;
        }

        if (!tokenData.accessToken) console.error('Token data missing accessToken:', tokenData);
        if (!tokenData.refreshToken) console.error('Token data missing refreshToken:', tokenData);
        if (!tokenData.userId) console.error('Token data missing userId:', tokenData);

        const tokenToSave = {
            userId: tokenData.userId,
            accessToken: tokenData.accessToken,
            refreshToken: tokenData.refreshToken
        };

        localStorage.setItem(API_CONFIG.STORAGE_KEYS.TOKEN, JSON.stringify(tokenToSave));
    },


    getTokens: () => {
        const tokensStr = localStorage.getItem(API_CONFIG.STORAGE_KEYS.TOKEN);
        if (!tokensStr) return null;

        try {
            const tokens = JSON.parse(tokensStr);
            if (!tokens.accessToken) console.error('Retrieved tokens missing accessToken:', tokens);
            if (!tokens.refreshToken) console.error('Retrieved tokens missing refreshToken:', tokens);
            if (!tokens.userId) console.error('Retrieved tokens missing userId:', tokens);
            return tokens;
        } catch (error) {
            console.error('Error parsing tokens from localStorage:', error);
            return null;
        }
    },


    removeTokens: () => localStorage.removeItem(API_CONFIG.STORAGE_KEYS.TOKEN),


    saveUserData: (userData) => localStorage.setItem(API_CONFIG.STORAGE_KEYS.USER, JSON.stringify(userData)),


    getUserData: () => {
        const userDataStr = localStorage.getItem(API_CONFIG.STORAGE_KEYS.USER);
        return userDataStr ? JSON.parse(userDataStr) : null;
    },


    removeUserData: () => localStorage.removeItem(API_CONFIG.STORAGE_KEYS.USER)
};

export default storageService;