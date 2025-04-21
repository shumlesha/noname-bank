export const generateIdempotencyKey = () => {
    if (crypto && crypto.randomUUID()) {
        return crypto.randomUUID();
    }

    return 'idemp-' + Math.random().toString(36).slice(2) + Date.now();
}