import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IReport } from 'app/shared/model/report.model';
import { getEntities as getReports } from 'app/entities/report/report.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './honk.reducer';
import { IHonk } from 'app/shared/model/honk.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IHonkUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IHonkUpdateState {
    isNew: boolean;
    reportId: string;
    userId: string;
}

export class HonkUpdate extends React.Component<IHonkUpdateProps, IHonkUpdateState> {
    constructor(props) {
        super(props);
        this.state = {
            reportId: '0',
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

        this.props.getReports();
        this.props.getUsers();
    }

    saveEntity = (event, errors, values) => {
        if (errors.length === 0) {
            const { honkEntity } = this.props;
            const entity = {
                ...honkEntity,
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
        this.props.history.push('/entity/honk');
    };

    render() {
        const { honkEntity, reports, users, loading, updating } = this.props;
        const { isNew } = this.state;

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.honk.home.createOrEditLabel">Create or edit a Honk</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : honkEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput id="honk-id" type="text" className="form-control" name="id" required readOnly />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="honkedLabel" check>
                                        <AvInput id="honk-honked" type="checkbox" className="form-control" name="honked" />
                                        Honked
                                    </Label>
                                </AvGroup>
                                <AvGroup>
                                    <Label for="report.id">Report</Label>
                                    <AvInput
                                        id="honk-report"
                                        type="select"
                                        className="form-control"
                                        name="report.id"
                                        value={isNew ? reports[0] && reports[0].id : honkEntity.report.id}
                                    >
                                        {reports
                                            ? reports.map(otherEntity => (
                                                  <option value={otherEntity.id} key={otherEntity.id}>
                                                      {otherEntity.id}
                                                  </option>
                                              ))
                                            : null}
                                    </AvInput>
                                </AvGroup>
                                <AvGroup>
                                    <Label for="user.login">User</Label>
                                    <AvInput
                                        id="honk-user"
                                        type="select"
                                        className="form-control"
                                        name="user.id"
                                        value={isNew ? users[0] && users[0].id : honkEntity.user.id}
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
                                <Button tag={Link} id="cancel-save" to="/entity/honk" replace color="info">
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
    reports: storeState.report.entities,
    users: storeState.userManagement.users,
    honkEntity: storeState.honk.entity,
    loading: storeState.honk.loading,
    updating: storeState.honk.updating,
    updateSuccess: storeState.honk.updateSuccess
});

const mapDispatchToProps = {
    getReports,
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
)(HonkUpdate);
