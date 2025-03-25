export const formatGenderValue = (gender) => {
    if (typeof gender === 'string') {
        return gender === 'MALE' ? 'Мужской' : gender === 'FEMALE' ? 'Женский' : gender;
    }

    if (gender && typeof gender === 'object') {
        if (gender.name) {
            return gender.name === 'MALE' ? 'Мужской' : gender.name === 'FEMALE' ? 'Женский' : gender.name;
        }

        if (gender.toString) {
            const genderStr = gender.toString();
            return genderStr === 'MALE' ? 'Мужской' : genderStr === 'FEMALE' ? 'Женский' : genderStr;
        }
    }

    return 'Не указано';
};


export const formatDate = (timestamp) => {
    if (!timestamp) return 'Нет данных';
    return new Date(timestamp).toLocaleString('ru-RU');
};


export const formatCurrency = (amount, currency = 'RUB') => {
    if (amount === undefined || amount === null) return 'Нет данных';
    
    let numAmount = amount;
    if (typeof amount === 'string') {
        numAmount = parseFloat(amount.replace(/[^\d.-]/g, ''));
    }
    
    if (isNaN(numAmount)) return 'Нет данных';
    
    return numAmount.toLocaleString('ru-RU', {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
};


export const getAccountStatus = (account) => {
    return account.closedTimestamp
        ? 'Закрыт'
        : account.blockedTimestamp
            ? 'Заблокирован'
            : 'Активен';
};


export const getAccountType = (account) => {
    return account.isCredit ? 'Кредитный' : 'Дебетовый';
};