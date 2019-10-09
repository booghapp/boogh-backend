import { IUser } from 'app/shared/model/user.model';

export interface IMemcache {
    id?: number;
    hash?: string;
    telegramId?: number;
    user?: IUser;
}

export const defaultValue: Readonly<IMemcache> = {};
