import {BaseModel} from './BaseModel.js';

export class User extends BaseModel {
    constructor(data = {}) {
        super(data);
        this.userId = data.userId || '';
        this.email = data.email || '';
        this.fullName = data.fullName || '';
        this.gender = data.gender || '';
        this.roles = data.roles || [];
    }
}
