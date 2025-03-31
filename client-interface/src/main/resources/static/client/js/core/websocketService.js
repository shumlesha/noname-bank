import {config} from '../config/config.js';
import {Transaction} from '../models/Transaction.js';

class WebSocketService {
    constructor() {
        this.socket = null;
        this.connected = false;
        this.listeners = {};
        this.transactions = [];
    }

    connect() {
        if (this.socket && this.connected) {
            return Promise.resolve();
        }

        return new Promise((resolve, reject) => {
            this.socket = new WebSocket(`ws://${window.location.host}${config.websocket.transaction}`);
            
            this.socket.onopen = () => {
                this.connected = true;
                console.log('Подключение WebSocket открыто');
                resolve();
            };
            
            this.socket.onclose = () => {
                this.connected = false;
                console.log('Подключение WebSocket закрыто');
            };
            
            this.socket.onerror = (error) => {
                console.error('WebSocket error:', error);
                reject(error);
            };
            
            this.socket.onmessage = (event) => {
                try {
                    const txData = JSON.parse(event.data);
                    const transaction = new Transaction(txData);

                    this.transactions.push(transaction);

                    if (this.listeners['transaction']) {
                        this.listeners['transaction'].forEach(callback => callback(this.transactions));
                    }
                } catch (error) {
                    console.error('Ошибка обработки сообщения WebSocket:', error);
                }
            };
        });
    }

    disconnect() {
        if (this.socket && this.connected) {
            this.socket.close();
            this.connected = false;
        }
    }

    async getTransactions(accountId) {
        this.transactions = [];
        
        await this.connect();
        
        return new Promise((resolve) => {
            const timeoutId = setTimeout(() => {
                resolve([...this.transactions]);
            }, 2000);

            const messageHandler = (event) => {
                try {
                    const txData = JSON.parse(event.data);
                    const transaction = new Transaction(txData);
                    this.transactions.push(transaction);
                } catch (error) {
                    console.error('Ошибка парсинга данных с WebSocket:', error);
                }
            };
            
            this.socket.addEventListener('message', messageHandler);

            this.socket.send(accountId);

            setTimeout(() => {
                this.socket.removeEventListener('message', messageHandler);
                clearTimeout(timeoutId);
            }, 2000);
        });
    }

    subscribe(accountId, callback) {
        if (!this.connected) {
            this.connect().then(() => this._doSubscribe(accountId, callback));
        } else {
            this._doSubscribe(accountId, callback);
        }
    }

    _doSubscribe(accountId, callback) {
        if (!this.listeners['transaction']) {
            this.listeners['transaction'] = [];
        }
        
        this.listeners['transaction'].push(callback);
        this.socket.send(accountId);
        console.log(`Подписано на транзакции аккаунта: ${accountId}`);
    }

    unsubscribe(accountId, callback) {
        if (this.listeners['transaction']) {
            this.listeners['transaction'] = this.listeners['transaction'].filter(cb => cb !== callback);
            console.log(`Отключено от транзакций аккаунта: ${accountId}`);
        }
    }

    clearTransactions() {
        this.transactions = [];
    }
}

export const websocketService = new WebSocketService(); 