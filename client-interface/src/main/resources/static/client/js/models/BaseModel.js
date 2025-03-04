export class BaseModel {

    constructor(data = {}) {
        this.setData(data);
    }

    setData(data) {
        Object.assign(this, data);
    }

    toJSON() {
        return { ...this };
    }
}
