import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import QA from './qa';
import QADetail from './qa-detail';
import QAUpdate from './qa-update';
import QADeleteDialog from './qa-delete-dialog';

const Routes = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/new`} component={QAUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={QAUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={QADetail} />
            <ErrorBoundaryRoute path={match.url} component={QA} />
        </Switch>
        <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={QADeleteDialog} />
    </>
);

export default Routes;
