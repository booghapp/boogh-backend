import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './reporter.reducer';
import { IReporter } from 'app/shared/model/reporter.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IReporterUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IReporterUpdateState {
    isNew: boolean;
    userId: string;
}

export class ReporterUpdate extends React.Component<IReporterUpdateProps, IReporterUpdateState> {
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

    onBlobChange = (isAnImage, name) => event => {
        setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
    };

    clearBlob = name => () => {
        this.props.setBlob(name, undefined, undefined);
    };

    saveEntity = (event, errors, values) => {
        if (errors.length === 0) {
            const { reporterEntity } = this.props;
            const entity = {
                ...reporterEntity,
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
        this.props.history.push('/entity/reporter');
    };

    render() {
        const { reporterEntity, users, loading, updating } = this.props;
        const { isNew } = this.state;

        const { about } = reporterEntity;

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.reporter.home.createOrEditLabel">Create or edit a Reporter</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : reporterEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput id="reporter-id" type="text" className="form-control" name="id" required readOnly />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="aboutLabel" for="about">
                                        About
                                    </Label>
                                    <AvInput id="reporter-about" type="textarea" name="about" />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="karmaLabel" for="karma">
                                        Karma
                                    </Label>
                                    <AvField id="reporter-karma" type="string" className="form-control" name="karma" />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="visibilityLabel" check>
                                        <AvInput id="reporter-visibility" type="checkbox" className="form-control" name="visibility" />
                                        Visibility
                                    </Label>
                                </AvGroup>
                                <AvGroup>
                                    <Label id="moderatorLabel" check>
                                        <AvInput id="reporter-moderator" type="checkbox" className="form-control" name="moderator" />
                                        Moderator
                                    </Label>
                                </AvGroup>
                                <AvGroup>
                                    <Label id="locationLabel" for="location">
                                        Location
                                    </Label>
                                    <AvField id="reporter-location" type="text" name="location" />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="notificationsOnLabel" check>
                                        <AvInput
                                            id="reporter-notificationsOn"
                                            type="checkbox"
                                            className="form-control"
                                            name="notificationsOn"
                                        />
                                        Notifications On
                                    </Label>
                                </AvGroup>
                                <AvGroup>
                                    <Label for="user.id">User</Label>
                                    <AvInput
                                        id="reporter-user"
                                        type="select"
                                        className="form-control"
                                        name="user.id"
                                        value={isNew ? users[0] && users[0].id : reporterEntity.user.id}
                                    >
                                        {users
                                            ? users.map(otherEntity => (
                                                  <option value={otherEntity.id} key={otherEntity.id}>
                                                      {otherEntity.id}
                                                  </option>
                                              ))
                                            : null}
                                    </AvInput>
                                </AvGroup>
                                <Button tag={Link} id="cancel-save" to="/entity/reporter" replace color="info">
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
    reporterEntity: storeState.reporter.entity,
    loading: storeState.reporter.loading,
    updating: storeState.reporter.updating,
    updateSuccess: storeState.reporter.updateSuccess
});

const mapDispatchToProps = {
    getUsers,
    getEntity,
    updateEntity,
    setBlob,
    createEntity,
    reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ReporterUpdate);
