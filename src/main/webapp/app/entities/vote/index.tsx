import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Vote from './vote';
import VoteDetail from './vote-detail';
import VoteUpdate from './vote-update';
import VoteDeleteDialog from './vote-delete-dialog';

const Routes = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/new`} component={VoteUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={VoteUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={VoteDetail} />
            <ErrorBoundaryRoute path={match.url} component={Vote} />
        </Switch>
        <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={VoteDeleteDialog} />
    </>
);

export default Routes;
