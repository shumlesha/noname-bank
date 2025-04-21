import {HomeController} from '../controllers/HomeController.js';
import pushService from "../core/pushService.js";

document.addEventListener('DOMContentLoaded', () => {
    new HomeController();
    pushService.init()
});
