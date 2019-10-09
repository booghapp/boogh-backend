import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './telegram-chat.reducer';
import { ITelegramChat } from 'app/shared/model/telegram-chat.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITelegramChatDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class TelegramChatDetail extends React.Component<ITelegramChatDetailProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    render() {
        const { telegramChatEntity } = this.props;
        return (
            <Row>
                <Col md="8">
                    <h2>
                        TelegramChat [<b>{telegramChatEntity.id}</b>]
                    </h2>
                    <dl className="jh-entity-details">
                        <dt>
                            <span id="chatId">Chat Id</span>
                        </dt>
                        <dd>{telegramChatEntity.chatId}</dd>
                        <dt>
                            <span id="telegramUserId">Telegram User Id</span>
                        </dt>
                        <dd>{telegramChatEntity.telegramUserId}</dd>
                        <dt>User</dt>
                        <dd>{telegramChatEntity.user ? telegramChatEntity.user.login : ''}</dd>
                    </dl>
                    <Button tag={Link} to="/entity/telegram-chat" replace color="info">
                        <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
                    </Button>
                    &nbsp;
                    <Button tag={Link} to={`/entity/telegram-chat/${telegramChatEntity.id}/edit`} replace color="primary">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                    </Button>
                </Col>
            </Row>
        );
    }
}

const mapStateToProps = ({ telegramChat }: IRootState) => ({
    telegramChatEntity: telegramChat.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(TelegramChatDetail);
