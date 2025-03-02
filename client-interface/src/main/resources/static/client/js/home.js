function initHomePage() {
    const logoutBtn = document.getElementById('logout-btn');
    const userEmailEl = document.getElementById('user-email');
    const userIdEl = document.getElementById('user-id');
    const userEmailDetailsEl = document.getElementById('user-email-details');

    const tokenData = storageService.getTokens();

    if (!tokenData) {
        window.location.href = '/client/login';
        return;
    }

    if (userIdEl) userIdEl.textContent = tokenData.userId;

    const userData = storageService.getUserData();
    if (userData && userData.email) {
        if (userEmailEl) userEmailEl.textContent = userData.email;
        if (userEmailDetailsEl) userEmailDetailsEl.textContent = userData.email;
    }

    logoutBtn.addEventListener('click', async () => {
        try {
            await apiService.logout();
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            storageService.removeTokens();
            storageService.removeUserData();
            window.location.href = '/client/login';
        }
    });
}
