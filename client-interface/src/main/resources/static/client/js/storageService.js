const storageService = {
    saveTokens: function(tokenData) {
        localStorage.setItem(TOKEN_STORAGE_KEY, JSON.stringify({
            userId: tokenData.userId,
            accessToken: tokenData.accessToken,
            refreshToken: tokenData.refreshToken
        }));
    },

    getTokens: function() {
        const tokensStr = localStorage.getItem(TOKEN_STORAGE_KEY);
        return tokensStr ? JSON.parse(tokensStr) : null;
    },

    removeTokens: function() {
        localStorage.removeItem(TOKEN_STORAGE_KEY);
    },

    saveUserData: function(userData) {
        localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(userData));
    },

    getUserData: function() {
        const userDataStr = localStorage.getItem(USER_STORAGE_KEY);
        return userDataStr ? JSON.parse(userDataStr) : null;
    },

    removeUserData: function() {
        localStorage.removeItem(USER_STORAGE_KEY);
    }
};
