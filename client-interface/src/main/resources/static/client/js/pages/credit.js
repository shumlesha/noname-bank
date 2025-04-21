import {CreditController} from '../controllers/CreditController.js';
import pushService from "../core/pushService.js";

document.addEventListener('DOMContentLoaded', () => {
    new CreditController();
    pushService.init()
});
