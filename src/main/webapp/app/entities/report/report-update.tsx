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
import { getEntities as getReports } from 'app/entities/report/report.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './report.reducer';
import { IReport } from 'app/shared/model/report.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IReportUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IReportUpdateState {
    isNew: boolean;
    reporterId: string;
    parentId: string;
}

export class ReportUpdate extends React.Component<IReportUpdateProps, IReportUpdateState> {
    constructor(props) {
        super(props);
        this.state = {
            reporterId: '0',
            parentId: '0',
            isNew: !this.props.match.params || !this.props.match.params.id
        };
        this.refresh = this.refresh.bind(this);
    }

    componentWillUpdate(nextProps, nextState) {
        if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
            this.handleClose();
        }
    }

    refresh(reportPromise) {
        reportPromise.then(this.setState({ isNew: this.state.isNew }));
    }

    componentDidMount() {
        if (this.state.isNew) {
            this.props.reset();
            this.props.getReports();
        } else {
            this.props.getEntity(this.props.match.params.id);
            const reportPromise = new Promise(() => this.props.getReports);
            this.refresh(reportPromise);
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
            const { reportEntity } = this.props;
            const entity = {
                ...reportEntity,
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
        this.props.history.push('/entity/report');
    };

    render() {
        const { reportEntity, users, reports, loading, updating } = this.props;
        const { isNew } = this.state;
        const { description, anonymous } = reportEntity;

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.report.home.createOrEditLabel">Create or edit a Report</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : reportEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput
                                            id="report-id"
                                            value={reportEntity.id}
                                            type="text"
                                            className="form-control"
                                            name="id"
                                            required
                                            readOnly
                                        />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="typeLabel">Type</Label>
                                    <AvInput
                                        id="report-type"
                                        type="select"
                                        className="form-control"
                                        name="type"
                                        value={(!isNew && reportEntity.type) || 'ROAD_SAFETY'}
                                    >
                                        <option value="ROAD_SAFETY">ROAD_SAFETY</option>
                                        <option value="EDUCATION">EDUCATION</option>
                                    </AvInput>
                                </AvGroup>
                                <AvGroup>
                                    <Label id="descriptionLabel" for="description">
                                        Description
                                    </Label>
                                    <AvInput
                                        id="report-description"
                                        type="textarea"
                                        name="description"
                                        value={reportEntity.description}
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' }
                                        }}
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="stateLabel">State</Label>
                                    <AvInput
                                        id="report-state"
                                        type="select"
                                        className="form-control"
                                        name="state"
                                        value={(!isNew && reportEntity.state) || 'PENDING'}
                                    >
                                        <option value="PENDING">PENDING</option>
                                        <option value="APPROVED">APPROVED</option>
                                        <option value="REJECTED">REJECTED</option>
                                    </AvInput>
                                </AvGroup>
                                <AvGroup>
                                    <Label id="anonymousLabel" check for="anonymous">
                                        <AvInput
                                            id="report-anonymous"
                                            type="checkbox"
                                            value={anonymous}
                                            className="form-control"
                                            name="anonymous"
                                        />
                                        Anonymous
                                    </Label>
                                </AvGroup>
                                <AvGroup>
                                    <Label id="latitudeLabel" for="latitude">
                                        Latitude
                                    </Label>
                                    <AvField
                                        id="report-latitude"
                                        type="string"
                                        value={reportEntity.latitude}
                                        className="form-control"
                                        name="latitude"
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="longitudeLabel" for="longitude">
                                        Longitude
                                    </Label>
                                    <AvField
                                        id="report-longitude"
                                        type="string"
                                        value={reportEntity.longitude}
                                        className="form-control"
                                        name="longitude"
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="dateLabel" for="date">
                                        Date
                                    </Label>
                                    <AvField id="report-date" type="date" value={reportEntity.date} className="form-control" name="date" />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="titleLabel" for="title">
                                        Title
                                    </Label>
                                    <AvField
                                        id="report-title"
                                        type="text"
                                        name="title"
                                        value={reportEntity.title}
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' }
                                        }}
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label for="reporter.login">Reporter</Label>
                                    <AvInput
                                        id="report-reporter"
                                        type="select"
                                        className="form-control"
                                        name="reporter.id"
                                        value={isNew ? users[0] && users[0].id : ''}
                                    >
                                        <option value="" key="0" />
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
                                    <Label for="parent.id">Parent</Label>
                                    <AvInput id="report-parent" type="select" className="form-control" name="parent.id">
                                        <option value="" key="0" />
                                        {reports
                                            ? reports.map(otherEntity => (
                                                  <option value={otherEntity.id} key={otherEntity.id}>
                                                      {otherEntity.id}
                                                  </option>
                                              ))
                                            : null}
                                    </AvInput>
                                </AvGroup>
                                <Button tag={Link} id="cancel-save" to="/entity/report" replace color="info">
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
    reportEntity: storeState.report.entity,
    loading: storeState.report.loading,
    updating: storeState.report.updating,
    updateSuccess: storeState.report.updateSuccess
});

const mapDispatchToProps = {
    getUsers,
    getReports,
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
)(ReportUpdate);
