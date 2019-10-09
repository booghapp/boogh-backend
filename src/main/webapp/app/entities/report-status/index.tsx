import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ReportStatus from './report-status';
import ReportStatusDetail from './report-status-detail';
import ReportStatusUpdate from './report-status-update';
import ReportStatusDeleteDialog from './report-status-delete-dialog';

const Routes = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ReportStatusUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ReportStatusUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ReportStatusDetail} />
            <ErrorBoundaryRoute path={match.url} component={ReportStatus} />
        </Switch>
        <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ReportStatusDeleteDialog} />
    </>
);

export default Routes;
