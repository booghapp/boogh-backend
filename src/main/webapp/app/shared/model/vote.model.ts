import { IUser } from 'app/shared/model/user.model';
import { IComment } from 'app/shared/model/comment.model';

export interface IVote {
    id?: number;
    vote?: number;
    voter?: IUser;
    comment?: IComment;
}

export const defaultValue: Readonly<IVote> = {
    voter: {},
    comment: {}
};
