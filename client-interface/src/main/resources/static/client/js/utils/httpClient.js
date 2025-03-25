export class HttpClient {
    static async fetch(url, options = {}) {

        const defaultOptions = {
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        };

        try {
            const response = await fetch(url, { ...defaultOptions, ...options });
            if (options.responseType === 'text') {
                return await response.text();
            }

            const text = await response.text();
            return text ? JSON.parse(text) : null;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    }
}
