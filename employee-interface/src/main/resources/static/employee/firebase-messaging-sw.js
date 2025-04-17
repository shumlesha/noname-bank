importScripts(
    'https://www.gstatic.com/firebasejs/11.6.0/firebase-app-compat.js',
    'https://www.gstatic.com/firebasejs/11.6.0/firebase-messaging-compat.js'
);

const firebaseConfig = {
    apiKey: "AIzaSyC2h2gIEQpeirXKARFFdD5FQYmxiu0sT0o",
    authDomain: "push-notification-507fa.firebaseapp.com",
    projectId: "push-notification-507fa",
    storageBucket: "push-notification-507fa.firebasestorage.app",
    messagingSenderId: "817325132448",
    appId: "1:817325132448:web:72f1dd2bb786560a6c17c5",
    measurementId: "G-QK3T9T7P7V"
};

firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
    console.log('Background message received:', payload);
    const notification = payload.notification;

    if (notification) {
        self.registration.showNotification(notification.title, {
            body: notification.body,
            icon: notification.icon || ''
        });
    }
});

self.addEventListener('activate', event => {
    console.log('Service Worker активирован');
    event.waitUntil(self.clients.claim());
});

self.addEventListener('install', event => {
    console.log('Service Worker установлен');
    self.skipWaiting();
});