import apiService from "./api-service.js";
import {showMessage, showPushNotification} from "../utils/ui-utils.js";

const firebaseConfig = {
    apiKey: "AIzaSyC2h2gIEQpeirXKARFFdD5FQYmxiu0sT0o",
    authDomain: "push-notification-507fa.firebaseapp.com",
    projectId: "push-notification-507fa",
    storageBucket: "push-notification-507fa.firebasestorage.app",
    messagingSenderId: "817325132448",
    appId: "1:817325132448:web:72f1dd2bb786560a6c17c5",
    measurementId: "G-QK3T9T7P7V"
};

const VAPID_KEY = 'BCtP5I8VDSoVLmmSad1V7bHJxv4uac7oW8SKN8GNbfM5KrcOKvGZgQsiihgSTpAJqw0xNARXyvbMeTcBJCeA-X0';
let messaging = null;

async function initFirebase() {
    console.log('Функция initFirebase НАЧАЛАСЬ.');
    try {
        const firebaseApp = await import('https://www.gstatic.com/firebasejs/11.6.0/firebase-app.js');
        const firebaseMessaging = await import('https://www.gstatic.com/firebasejs/11.6.0/firebase-messaging.js');

        console.log('Firebase модули импортированы успешно');

        const app = firebaseApp.initializeApp(firebaseConfig);

        const swReg = await navigator.serviceWorker.register('/employee/firebase-messaging-sw.js');
        console.log('SW registered', swReg);

        await navigator.serviceWorker.ready;
        console.log('Service Worker готов');

        messaging = firebaseMessaging.getMessaging(app);
        console.log('Firebase Messaging инициализирован');

        const permission = await Notification.requestPermission();
        console.log('Notification permission:', permission);

        if (permission !== 'granted') {
            console.error('Разрешение на уведомления не предоставлено');
            return false;
        }

        const cached = localStorage.getItem('fcm_token');
        const current = await firebaseMessaging.getToken(messaging, {
            vapidKey: VAPID_KEY,
            serviceWorkerRegistration: swReg,
        });

        console.log('Token result:', current);

        if (current && current !== cached) {
            try {
                await apiService.registerPushToken(current);
                localStorage.setItem('fcm_token', current);
                console.log('Токен успешно зарегистрирован на сервере и сохранен локально');
            } catch (error) {
                console.error('Ошибка при регистрации токена на сервере:', error);
            }
        } else {
            console.log('Используется существующий токен из кэша');
        }

        firebaseMessaging.onMessage(messaging, (payload) => {
            const n    = payload.notification ?? {};
            const data = payload.data ?? {};
            const ttl  = Number(data.ttl) || 5;

            showPushNotification({
                title: n.title ?? 'Уведомление',
                body : n.body  ?? '',
                icon : n.icon  ?? '',
                ttl
            });
        });

        return true;
    } catch (error) {
        console.error('Ошибка при инициализации Firebase:', error);
        return false;
    }
}



export default {
    async init() {
        console.log('Вызов init() из экспортируемого модуля...');
        try {
            const result = await initFirebase();
            console.log('Результат инициализации Firebase:', result);
            return result;
        } catch (error) {
            console.error("Критическая ошибка инициализации Firebase:", error);
            return false;
        }
    }
}