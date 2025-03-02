function checkAuth() {
    const tokenData = storageService.getTokens();

    if (!tokenData) {
        window.location.href = '/client/login';
        return false;
    }

    return true;
}

document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;

    if (currentPath !== '/client/login' && currentPath !== '/client/register') {
        checkAuth();
    }
});
