import { Moment } from 'moment';
import { IUser } from 'app/shared/model/user.model';
import { IReport } from 'app/shared/model/report.model';
import { IComment } from 'app/shared/model/comment.model';
import { IVote } from 'app/shared/model/vote.model';

export interface IComment {
    id?: number;
    content?: any;
    date?: Moment;
    commenter?: IUser;
    report?: IReport;
    parent?: IComment;
    votes?: IVote[];
}

export const defaultValue: Readonly<IComment> = {
    commenter: {},
    report: {}
};
