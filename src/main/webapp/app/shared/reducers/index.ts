import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from 'app/modules/administration/user-management/user-management.reducer';
import register, { RegisterState } from 'app/modules/account/register/register.reducer';
import activate, { ActivateState } from 'app/modules/account/activate/activate.reducer';
import password, { PasswordState } from 'app/modules/account/password/password.reducer';
import settings, { SettingsState } from 'app/modules/account/settings/settings.reducer';
import passwordReset, { PasswordResetState } from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import comment, {
  CommentState
} from 'app/entities/comment/comment.reducer';
// prettier-ignore
import report, {
  ReportState
} from 'app/entities/report/report.reducer';
// prettier-ignore
import reportStatus, {
  ReportStatusState
} from 'app/entities/report-status/report-status.reducer';
// prettier-ignore
import reporter, {
  ReporterState
} from 'app/entities/reporter/reporter.reducer';
// prettier-ignore
import vote, {
  VoteState
} from 'app/entities/vote/vote.reducer';
// prettier-ignore
import honk, {
  HonkState
} from 'app/entities/honk/honk.reducer';
// prettier-ignore
import document, {
  DocumentState
} from 'app/entities/document/document.reducer';
// prettier-ignore
import qA, {
  QAState
} from 'app/entities/qa/qa.reducer';
// prettier-ignore
import telegramChat, {
  TelegramChatState
} from 'app/entities/telegram-chat/telegram-chat.reducer';
// prettier-ignore
import feedback, {
  FeedbackState
} from 'app/entities/feedback/feedback.reducer';
// prettier-ignore
import memcache, {
  MemcacheState
} from 'app/entities/memcache/memcache.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

export interface IRootState {
    readonly authentication: AuthenticationState;
    readonly applicationProfile: ApplicationProfileState;
    readonly administration: AdministrationState;
    readonly userManagement: UserManagementState;
    readonly register: RegisterState;
    readonly activate: ActivateState;
    readonly passwordReset: PasswordResetState;
    readonly password: PasswordState;
    readonly settings: SettingsState;
    readonly comment: CommentState;
    readonly report: ReportState;
    readonly reportStatus: ReportStatusState;
    readonly reporter: ReporterState;
    readonly vote: VoteState;
    readonly honk: HonkState;
    readonly document: DocumentState;
    readonly qA: QAState;
    readonly telegramChat: TelegramChatState;
    readonly feedback: FeedbackState;
    readonly memcache: MemcacheState;
    /* jhipster-needle-add-reducer-type - JHipster will add reducer type here */
    readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
    authentication,
    applicationProfile,
    administration,
    userManagement,
    register,
    activate,
    passwordReset,
    password,
    settings,
    comment,
    report,
    reportStatus,
    reporter,
    vote,
    honk,
    document,
    qA,
    telegramChat,
    feedback,
    memcache,
    /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
    loadingBar
});

export default rootReducer;
