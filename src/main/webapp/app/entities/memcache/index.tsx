import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Memcache from './memcache';
import MemcacheDetail from './memcache-detail';
import MemcacheUpdate from './memcache-update';
import MemcacheDeleteDialog from './memcache-delete-dialog';

const Routes = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MemcacheUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MemcacheUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MemcacheDetail} />
            <ErrorBoundaryRoute path={match.url} component={Memcache} />
        </Switch>
        <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={MemcacheDeleteDialog} />
    </>
);

export default Routes;
