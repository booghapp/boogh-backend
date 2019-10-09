import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Honk from './honk';
import HonkDetail from './honk-detail';
import HonkUpdate from './honk-update';
import HonkDeleteDialog from './honk-delete-dialog';

const Routes = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/new`} component={HonkUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={HonkUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={HonkDetail} />
            <ErrorBoundaryRoute path={match.url} component={Honk} />
        </Switch>
        <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={HonkDeleteDialog} />
    </>
);

export default Routes;
