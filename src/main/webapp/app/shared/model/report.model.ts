import { Moment } from 'moment';
import { IComment } from 'app/shared/model/comment.model';
import { IUser } from 'app/shared/model/user.model';
import { IReportStatus } from 'app/shared/model/report-status.model';
import { IReport } from 'app/shared/model/report.model';
import { IHonk } from 'app/shared/model/honk.model';

export const enum ReportType {
    ROAD_SAFETY = 'ROAD_SAFETY',
    EDUCATION = 'EDUCATION'
}

export const enum ReportState {
    PENDING = 'PENDING',
    APPROVED = 'APPROVED',
    REJECTED = 'REJECTED'
}

export interface IReport {
    id?: number;
    type?: ReportType;
    description?: any;
    state?: ReportState;
    anonymous?: boolean;
    latitude?: number;
    longitude?: number;
    date?: Moment;
    title?: string;
    comments?: IComment[];
    reporter?: IUser;
    reportStatuses?: IReportStatus[];
    parent?: IReport;
    images?: string[];
    honks?: IHonk[];
}

export const defaultValue: Readonly<IReport> = {
    reporter: {}
};
