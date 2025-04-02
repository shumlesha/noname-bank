import {initAccountsPage} from './pages/accounts-page.js';
import {initProfilePage} from './pages/profile-page.js';
import {initUsersPage} from './pages/users-page.js';
import {initClientDetailsPage} from './pages/client-details-page.js';
import {initCommonUI} from './components/common-ui.js';
import {initCreditTariffsPage} from './pages/credit-tariffs-page.js';
import {checkAuth} from './auth.js';


document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;

    if (!currentPath.includes('/login') && !currentPath.includes('/register')) {
        initCommonUI();
    }

    switch (currentPath) {
        // case '/employee/login':
        //     initLoginPage();
        //     break;
        //
        // case '/employee/register':
        //     initRegisterPage();
        //     break;

        case '/employee/me':
            initProfilePage();
            break;

        case '/employee/accounts':
            initAccountsPage();
            break;
            
        case '/employee/users':
            initUsersPage();
            break;

        case '/employee/credit-tariffs':
            initCreditTariffsPage();
            break;

        default:
            if (currentPath.startsWith('/employee/client/')) {
                initClientDetailsPage();
            } else if (currentPath.startsWith('/employee/')) {
                checkAuth();
            }
            break;
    }
});