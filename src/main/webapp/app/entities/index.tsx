import React from 'react';
import { Switch } from 'react-router-dom';

// tslint:disable-next-line:no-unused-variable
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Comment from './comment';
import Report from './report';
import ReportStatus from './report-status';
import Reporter from './reporter';
import Vote from './vote';
import Honk from './honk';
import Document from './document';
import QA from './qa';
import TelegramChat from './telegram-chat';
import Feedback from './feedback';
import Memcache from './memcache';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
    <div>
        <Switch>
            {/* prettier-ignore */}
            <ErrorBoundaryRoute path={`${match.url}/comment`} component={Comment} />
            <ErrorBoundaryRoute path={`${match.url}/report`} component={Report} />
            <ErrorBoundaryRoute path={`${match.url}/report-status`} component={ReportStatus} />
            <ErrorBoundaryRoute path={`${match.url}/reporter`} component={Reporter} />
            <ErrorBoundaryRoute path={`${match.url}/vote`} component={Vote} />
            <ErrorBoundaryRoute path={`${match.url}/honk`} component={Honk} />
            <ErrorBoundaryRoute path={`${match.url}/document`} component={Document} />
            <ErrorBoundaryRoute path={`${match.url}/qa`} component={QA} />
            <ErrorBoundaryRoute path={`${match.url}/telegram-chat`} component={TelegramChat} />
            <ErrorBoundaryRoute path={`${match.url}/feedback`} component={Feedback} />
            <ErrorBoundaryRoute path={`${match.url}/memcache`} component={Memcache} />
            {/* jhipster-needle-add-route-path - JHipster will routes here */}
        </Switch>
    </div>
);

export default Routes;
