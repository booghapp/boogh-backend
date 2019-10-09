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
import { IReport } from 'app/shared/model/report.model';
import { getEntities as getReports } from 'app/entities/report/report.reducer';
import { getEntity, updateEntity, createEntity, reset } from './report-status.reducer';
import { IReportStatus } from 'app/shared/model/report-status.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IReportStatusUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IReportStatusUpdateState {
    isNew: boolean;
    reporterId: string;
    reportId: string;
}

export class ReportStatusUpdate extends React.Component<IReportStatusUpdateProps, IReportStatusUpdateState> {
    constructor(props) {
        super(props);
        this.state = {
            reporterId: '0',
            reportId: '0',
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
        this.props.getReports();
    }

    saveEntity = (event, errors, values) => {
        if (errors.length === 0) {
            const { reportStatusEntity } = this.props;
            const entity = {
                ...reportStatusEntity,
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
        this.props.history.push('/entity/report-status');
    };

    render() {
        const { reportStatusEntity, users, reports, loading, updating } = this.props;
        const { isNew } = this.state;

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.reportStatus.home.createOrEditLabel">Create or edit a ReportStatus</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : reportStatusEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput id="report-status-id" type="text" className="form-control" name="id" required readOnly />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="savedLabel">Saved</Label>
                                    <AvInput
                                        id="report-status-saved"
                                        type="select"
                                        className="form-control"
                                        name="saved"
                                        value={(!isNew && reportStatusEntity.saved) || 'UNSET'}
                                    >
                                        <option value="UNSET">UNSET</option>
                                        <option value="TRUE">TRUE</option>
                                        <option value="FALSE">FALSE</option>
                                    </AvInput>
                                </AvGroup>
                                <AvGroup>
                                    <Label id="flaggedLabel">Flagged</Label>
                                    <AvInput
                                        id="report-status-flagged"
                                        type="select"
                                        className="form-control"
                                        name="flagged"
                                        value={(!isNew && reportStatusEntity.flagged) || 'UNSET'}
                                    >
                                        <option value="UNSET">UNSET</option>
                                        <option value="TRUE">TRUE</option>
                                        <option value="FALSE">FALSE</option>
                                    </AvInput>
                                </AvGroup>
                                <AvGroup>
                                    <Label for="reporter.login">Reporter</Label>
                                    <AvInput
                                        id="report-status-reporter"
                                        type="select"
                                        className="form-control"
                                        name="reporter.id"
                                        value={isNew ? users[0] && users[0].id : reportStatusEntity.reporter.id}
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
                                <AvGroup>
                                    <Label for="report.id">Report</Label>
                                    <AvInput
                                        id="report-status-report"
                                        type="select"
                                        className="form-control"
                                        name="report.id"
                                        value={isNew ? reports[0] && reports[0].id : reportStatusEntity.report.id}
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
                                <Button tag={Link} id="cancel-save" to="/entity/report-status" replace color="info">
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
    reports: storeState.report.entities,
    reportStatusEntity: storeState.reportStatus.entity,
    loading: storeState.reportStatus.loading,
    updating: storeState.reportStatus.updating,
    updateSuccess: storeState.reportStatus.updateSuccess
});

const mapDispatchToProps = {
    getUsers,
    getReports,
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
)(ReportStatusUpdate);
