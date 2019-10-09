import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './report-status.reducer';
import { IReportStatus } from 'app/shared/model/report-status.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IReportStatusDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ReportStatusDetail extends React.Component<IReportStatusDetailProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    render() {
        const { reportStatusEntity } = this.props;
        return (
            <Row>
                <Col md="8">
                    <h2>
                        ReportStatus [<b>{reportStatusEntity.id}</b>]
                    </h2>
                    <dl className="jh-entity-details">
                        <dt>
                            <span id="saved">Saved</span>
                        </dt>
                        <dd>{reportStatusEntity.saved}</dd>
                        <dt>
                            <span id="flagged">Flagged</span>
                        </dt>
                        <dd>{reportStatusEntity.flagged}</dd>
                        <dt>Reporter</dt>
                        <dd>{reportStatusEntity.reporter ? reportStatusEntity.reporter.login : ''}</dd>
                        <dt>Report</dt>
                        <dd>{reportStatusEntity.report ? reportStatusEntity.report.id : ''}</dd>
                    </dl>
                    <Button tag={Link} to="/entity/report-status" replace color="info">
                        <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
                    </Button>
                    &nbsp;
                    <Button tag={Link} to={`/entity/report-status/${reportStatusEntity.id}/edit`} replace color="primary">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                    </Button>
                </Col>
            </Row>
        );
    }
}

const mapStateToProps = ({ reportStatus }: IRootState) => ({
    reportStatusEntity: reportStatus.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ReportStatusDetail);
