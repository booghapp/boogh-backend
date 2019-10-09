import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAllAction, getSortState, IPaginationBaseState, getPaginationItemsNumber, JhiPagination } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './report-status.reducer';
import { IReportStatus } from 'app/shared/model/report-status.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface IReportStatusProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type IReportStatusState = IPaginationBaseState;

export class ReportStatus extends React.Component<IReportStatusProps, IReportStatusState> {
    state: IReportStatusState = {
        ...getSortState(this.props.location, ITEMS_PER_PAGE)
    };

    componentDidMount() {
        this.getEntities();
    }

    sort = prop => () => {
        this.setState(
            {
                order: this.state.order === 'asc' ? 'desc' : 'asc',
                sort: prop
            },
            () => this.sortEntities()
        );
    };

    sortEntities() {
        this.getEntities();
        this.props.history.push(
            `${this.props.location.pathname}?page=${this.state.activePage}&sort=${this.state.sort},${this.state.order}`
        );
    }

    handlePagination = activePage => this.setState({ activePage }, () => this.sortEntities());

    getEntities = () => {
        const { activePage, itemsPerPage, sort, order } = this.state;
        this.props.getEntities(activePage - 1, itemsPerPage, `${sort},${order}`);
    };

    render() {
        const { reportStatusList, match, totalItems } = this.props;
        return (
            <div>
                <h2 id="report-status-heading">
                    Report Statuses
                    <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
                        <FontAwesomeIcon icon="plus" />
                        &nbsp; Create new Report Status
                    </Link>
                </h2>
                <div className="table-responsive">
                    <Table responsive>
                        <thead>
                            <tr>
                                <th className="hand" onClick={this.sort('id')}>
                                    ID <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('saved')}>
                                    Saved <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('flagged')}>
                                    Flagged <FontAwesomeIcon icon="sort" />
                                </th>
                                <th>
                                    Reporter <FontAwesomeIcon icon="sort" />
                                </th>
                                <th>
                                    Report <FontAwesomeIcon icon="sort" />
                                </th>
                                <th />
                            </tr>
                        </thead>
                        <tbody>
                            {reportStatusList.map((reportStatus, i) => (
                                <tr key={`entity-${i}`}>
                                    <td>
                                        <Button tag={Link} to={`${match.url}/${reportStatus.id}`} color="link" size="sm">
                                            {reportStatus.id}
                                        </Button>
                                    </td>
                                    <td>{reportStatus.saved}</td>
                                    <td>{reportStatus.flagged}</td>
                                    <td>{reportStatus.reporter ? reportStatus.reporter.login : ''}</td>
                                    <td>
                                        {reportStatus.report ? (
                                            <Link to={`report/${reportStatus.report.id}`}>{reportStatus.report.id}</Link>
                                        ) : (
                                            ''
                                        )}
                                    </td>
                                    <td className="text-right">
                                        <div className="btn-group flex-btn-group-container">
                                            <Button tag={Link} to={`${match.url}/${reportStatus.id}`} color="info" size="sm">
                                                <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${reportStatus.id}/edit`} color="primary" size="sm">
                                                <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${reportStatus.id}/delete`} color="danger" size="sm">
                                                <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                                            </Button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </div>
                <Row className="justify-content-center">
                    <JhiPagination
                        items={getPaginationItemsNumber(totalItems, this.state.itemsPerPage)}
                        activePage={this.state.activePage}
                        onSelect={this.handlePagination}
                        maxButtons={5}
                    />
                </Row>
            </div>
        );
    }
}

const mapStateToProps = ({ reportStatus }: IRootState) => ({
    reportStatusList: reportStatus.entities,
    totalItems: reportStatus.totalItems
});

const mapDispatchToProps = {
    getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ReportStatus);
