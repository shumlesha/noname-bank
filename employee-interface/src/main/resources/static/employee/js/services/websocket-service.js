const websocketService = {
    socket: null,
    callbacks: {},
    connected: false,
    reconnectAttempts: 0,
    maxReconnectAttempts: 5,
    reconnectTimeout: null,
    

    connect: function(url) {
        return new Promise((resolve, reject) => {
            if (this.socket && this.socket.readyState === WebSocket.OPEN) {
                console.log('WebSocket already connected');
                resolve();
                return;
            }
            
            try {
                this.socket = new WebSocket(url);
                
                this.socket.onopen = () => {
                    console.log('WebSocket connection established');
                    this.connected = true;
                    this.reconnectAttempts = 0;
                    resolve();
                };
                
                this.socket.onclose = (event) => {
                    console.log('WebSocket connection closed', event);
                    this.connected = false;

                    if (!event.wasClean && this.reconnectAttempts < this.maxReconnectAttempts) {
                        this.reconnectAttempts++;
                        const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
                        console.log(`Attempting to reconnect in ${delay}ms (attempt ${this.reconnectAttempts})`);
                        
                        clearTimeout(this.reconnectTimeout);
                        this.reconnectTimeout = setTimeout(() => {
                            this.connect(url).catch(err => {
                                console.error('Failed to reconnect:', err);
                            });
                        }, delay);
                    }
                };
                
                this.socket.onerror = (error) => {
                    console.error('WebSocket error:', error);
                    if (!this.connected) {
                        reject(error);
                    }
                };
                
                this.socket.onmessage = (event) => {
                    try {
                        const data = JSON.parse(event.data);
                        console.log('WebSocket message received:', data);

                        Object.values(this.callbacks).forEach(callback => {
                            try {
                                callback(data);
                            } catch (err) {
                                console.error('Error in WebSocket callback:', err);
                            }
                        });
                    } catch (err) {
                        console.error('Error parsing WebSocket message:', err);
                    }
                };
            } catch (error) {
                console.error('Error creating WebSocket connection:', error);
                reject(error);
            }
        });
    },
    

    disconnect: function() {
        if (this.socket) {
            this.socket.close();
            this.socket = null;
            this.connected = false;
            this.callbacks = {};
            clearTimeout(this.reconnectTimeout);
        }
    },
    

    send: function(message) {
        if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
            console.error('Cannot send message: WebSocket not connected');
            return false;
        }
        
        try {
            const messageStr = typeof message === 'string' ? message : JSON.stringify(message);
            this.socket.send(messageStr);
            return true;
        } catch (error) {
            console.error('Error sending WebSocket message:', error);
            return false;
        }
    },
    

    subscribe: function(id, callback) {
        if (typeof callback !== 'function') {
            console.error('Callback must be a function');
            return;
        }
        this.callbacks[id] = callback;
    },
    

    unsubscribe: function(id) {
        delete this.callbacks[id];
    },
    

    isConnected: function() {
        return this.connected && this.socket && this.socket.readyState === WebSocket.OPEN;
    }
};

export default websocketService;
