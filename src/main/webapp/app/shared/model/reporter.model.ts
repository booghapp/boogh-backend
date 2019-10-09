import { IUser } from 'app/shared/model/user.model';

export interface IReporter {
    id?: number;
    about?: any;
    karma?: number;
    visibility?: boolean;
    moderator?: boolean;
    location?: string;
    notificationsOn?: boolean;
    user?: IUser;
}

export const defaultValue: Readonly<IReporter> = {
    user: {}
};
