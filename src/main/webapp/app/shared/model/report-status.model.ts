import { IUser } from 'app/shared/model/user.model';
import { IReport } from 'app/shared/model//report.model';

export const enum ReportStatusState {
    UNSET = 'UNSET',
    TRUE = 'TRUE',
    FALSE = 'FALSE'
}

export interface IReportStatus {
    id?: number;
    saved?: ReportStatusState;
    flagged?: ReportStatusState;
    reporter?: IUser;
    report?: IReport;
}

export const defaultValue: Readonly<IReportStatus> = {
    reporter: {},
    report: {}
};
