import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './telegram-chat.reducer';
import { ITelegramChat } from 'app/shared/model/telegram-chat.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ITelegramChatUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface ITelegramChatUpdateState {
    isNew: boolean;
    userId: string;
}

export class TelegramChatUpdate extends React.Component<ITelegramChatUpdateProps, ITelegramChatUpdateState> {
    constructor(props) {
        super(props);
        this.state = {
            userId: '0',
            isNew: !this.props.match.params || !this.props.match.params.id
        };
    }

    componentWillUpdate(nextProps, nextState) {
        if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
            this.handleClose();
        }
    }

    componentDidMount() {
        if (this.state.isNew) {
            this.props.reset();
        } else {
            this.props.getEntity(this.props.match.params.id);
        }

        this.props.getUsers();
    }

    saveEntity = (event, errors, values) => {
        if (errors.length === 0) {
            const { telegramChatEntity } = this.props;
            const entity = {
                ...telegramChatEntity,
                ...values
            };

            if (this.state.isNew) {
                this.props.createEntity(entity);
            } else {
                this.props.updateEntity(entity);
            }
        }
    };

    handleClose = () => {
        this.props.history.push('/entity/telegram-chat');
    };

    render() {
        const { telegramChatEntity, users, loading, updating } = this.props;
        const { isNew } = this.state;

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.telegramChat.home.createOrEditLabel">Create or edit a TelegramChat</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : telegramChatEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput id="telegram-chat-id" type="text" className="form-control" name="id" required readOnly />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="chatIdLabel" for="chatId">
                                        Chat Id
                                    </Label>
                                    <AvField
                                        id="telegram-chat-chatId"
                                        type="string"
                                        className="form-control"
                                        name="chatId"
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' },
                                            number: { value: true, errorMessage: 'This field should be a number.' }
                                        }}
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="telegramUserIdLabel" for="telegramUserId">
                                        Telegram User Id
                                    </Label>
                                    <AvField
                                        id="telegram-chat-telegramUserId"
                                        type="string"
                                        className="form-control"
                                        name="telegramUserId"
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' },
                                            number: { value: true, errorMessage: 'This field should be a number.' }
                                        }}
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label for="user.login">User</Label>
                                    <AvInput
                                        id="telegram-chat-user"
                                        type="select"
                                        className="form-control"
                                        name="user.id"
                                        value={isNew ? users[0] && users[0].id : telegramChatEntity.user.id}
                                    >
                                        {users
                                            ? users.map(otherEntity => (
                                                  <option value={otherEntity.id} key={otherEntity.id}>
                                                      {otherEntity.login}
                                                  </option>
                                              ))
                                            : null}
                                    </AvInput>
                                </AvGroup>
                                <Button tag={Link} id="cancel-save" to="/entity/telegram-chat" replace color="info">
                                    <FontAwesomeIcon icon="arrow-left" />
                                    &nbsp;
                                    <span className="d-none d-md-inline">Back</span>
                                </Button>
                                &nbsp;
                                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                                    <FontAwesomeIcon icon="save" />
                                    &nbsp; Save
                                </Button>
                            </AvForm>
                        )}
                    </Col>
                </Row>
            </div>
        );
    }
}

const mapStateToProps = (storeState: IRootState) => ({
    users: storeState.userManagement.users,
    telegramChatEntity: storeState.telegramChat.entity,
    loading: storeState.telegramChat.loading,
    updating: storeState.telegramChat.updating,
    updateSuccess: storeState.telegramChat.updateSuccess
});

const mapDispatchToProps = {
    getUsers,
    getEntity,
    updateEntity,
    createEntity,
    reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(TelegramChatUpdate);
