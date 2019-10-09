import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAllAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './telegram-chat.reducer';
import { ITelegramChat } from 'app/shared/model/telegram-chat.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITelegramChatProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export class TelegramChat extends React.Component<ITelegramChatProps> {
    componentDidMount() {
        this.props.getEntities();
    }

    render() {
        const { telegramChatList, match } = this.props;
        return (
            <div>
                <h2 id="telegram-chat-heading">
                    Telegram Chats
                    <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
                        <FontAwesomeIcon icon="plus" />
                        &nbsp; Create new Telegram Chat
                    </Link>
                </h2>
                <div className="table-responsive">
                    <Table responsive>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Chat Id</th>
                                <th>Telegram User Id</th>
                                <th>User</th>
                                <th />
                            </tr>
                        </thead>
                        <tbody>
                            {telegramChatList.map((telegramChat, i) => (
                                <tr key={`entity-${i}`}>
                                    <td>
                                        <Button tag={Link} to={`${match.url}/${telegramChat.id}`} color="link" size="sm">
                                            {telegramChat.id}
                                        </Button>
                                    </td>
                                    <td>{telegramChat.chatId}</td>
                                    <td>{telegramChat.telegramUserId}</td>
                                    <td>{telegramChat.user ? telegramChat.user.login : ''}</td>
                                    <td className="text-right">
                                        <div className="btn-group flex-btn-group-container">
                                            <Button tag={Link} to={`${match.url}/${telegramChat.id}`} color="info" size="sm">
                                                <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${telegramChat.id}/edit`} color="primary" size="sm">
                                                <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${telegramChat.id}/delete`} color="danger" size="sm">
                                                <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                                            </Button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </div>
            </div>
        );
    }
}

const mapStateToProps = ({ telegramChat }: IRootState) => ({
    telegramChatList: telegramChat.entities
});

const mapDispatchToProps = {
    getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(TelegramChat);
