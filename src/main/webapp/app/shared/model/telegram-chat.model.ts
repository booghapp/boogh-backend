import { IUser } from 'app/shared/model/user.model';

export interface ITelegramChat {
    id?: number;
    chatId?: number;
    telegramUserId?: number;
    user?: IUser;
}

export const defaultValue: Readonly<ITelegramChat> = {
    user: {}
};
