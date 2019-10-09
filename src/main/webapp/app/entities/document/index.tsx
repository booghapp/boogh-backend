import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Document from './document';
import DocumentDetail from './document-detail';
import DocumentUpdate from './document-update';
import DocumentDeleteDialog from './document-delete-dialog';

const Routes = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/new`} component={DocumentUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={DocumentUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={DocumentDetail} />
            <ErrorBoundaryRoute path={match.url} component={Document} />
        </Switch>
        <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={DocumentDeleteDialog} />
    </>
);

export default Routes;
