import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './reporter.reducer';
import { IReporter } from 'app/shared/model/reporter.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IReporterDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ReporterDetail extends React.Component<IReporterDetailProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    render() {
        const { reporterEntity } = this.props;
        return (
            <Row>
                <Col md="8">
                    <h2>
                        Reporter [<b>{reporterEntity.id}</b>]
                    </h2>
                    <dl className="jh-entity-details">
                        <dt>
                            <span id="about">About</span>
                        </dt>
                        <dd>{reporterEntity.about}</dd>
                        <dt>
                            <span id="karma">Karma</span>
                        </dt>
                        <dd>{reporterEntity.karma}</dd>
                        <dt>
                            <span id="visibility">Visibility</span>
                        </dt>
                        <dd>{reporterEntity.visibility ? 'true' : 'false'}</dd>
                        <dt>
                            <span id="moderator">Moderator</span>
                        </dt>
                        <dd>{reporterEntity.moderator ? 'true' : 'false'}</dd>
                        <dt>
                            <span id="location">Location</span>
                        </dt>
                        <dd>{reporterEntity.location}</dd>
                        <dt>
                            <span id="notificationsOn">Notifications On</span>
                        </dt>
                        <dd>{reporterEntity.notificationsOn ? 'true' : 'false'}</dd>
                        <dt>User</dt>
                        <dd>{reporterEntity.user ? reporterEntity.user.id : ''}</dd>
                    </dl>
                    <Button tag={Link} to="/entity/reporter" replace color="info">
                        <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
                    </Button>
                    &nbsp;
                    <Button tag={Link} to={`/entity/reporter/${reporterEntity.id}/edit`} replace color="primary">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                    </Button>
                </Col>
            </Row>
        );
    }
}

const mapStateToProps = ({ reporter }: IRootState) => ({
    reporterEntity: reporter.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ReporterDetail);
