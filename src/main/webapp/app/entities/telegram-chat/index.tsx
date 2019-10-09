import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import TelegramChat from './telegram-chat';
import TelegramChatDetail from './telegram-chat-detail';
import TelegramChatUpdate from './telegram-chat-update';
import TelegramChatDeleteDialog from './telegram-chat-delete-dialog';

const Routes = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TelegramChatUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TelegramChatUpdate} />
            <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TelegramChatDetail} />
            <ErrorBoundaryRoute path={match.url} component={TelegramChat} />
        </Switch>
        <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={TelegramChatDeleteDialog} />
    </>
);

export default Routes;
