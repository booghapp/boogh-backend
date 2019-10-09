import { IReport } from 'app/shared/model/report.model';
import { IUser } from 'app/shared/model/user.model';

export interface IHonk {
    id?: number;
    honked?: boolean;
    report?: IReport;
    user?: IUser;
}

export const defaultValue: Readonly<IHonk> = {
    report: {},
    user: {},
    honked: false
};
