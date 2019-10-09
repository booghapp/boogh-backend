import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Reporter from './reporter';
import ReporterDetail from './reporter-detail';
import ReporterUpdate from './reporter-update';
import ReporterDeleteDialog from './reporter-delete-dialog';

const Routes = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ReporterUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ReporterUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ReporterDetail} />
            <ErrorBoundaryRoute path={match.url} component={Reporter} />
        </Switch>
        <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ReporterDeleteDialog} />
    </>
);

export default Routes;
