import { Moment } from 'moment';

export interface IFeedback {
    id?: number;
    content?: any;
    createdOn?: Moment;
}

export const defaultValue: Readonly<IFeedback> = {};
