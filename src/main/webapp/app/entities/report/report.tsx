import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import {
    byteSize,
    ICrudGetAllAction,
    TextFormat,
    getSortState,
    IPaginationBaseState,
    getPaginationItemsNumber,
    JhiPagination
} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './report.reducer';
import { IReport } from 'app/shared/model/report.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface IReportProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type IReportState = IPaginationBaseState;

export class Report extends React.Component<IReportProps, IReportState> {
    state: IReportState = {
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
        const { reportList, match, totalItems } = this.props;
        return (
            <div>
                <h2 id="report-heading">
                    Reports
                    <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
                        <FontAwesomeIcon icon="plus" />
                        &nbsp; Create new Report
                    </Link>
                </h2>
                <div className="table-responsive">
                    <Table responsive>
                        <thead>
                            <tr>
                                <th className="hand" onClick={this.sort('id')}>
                                    ID <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('type')}>
                                    Type <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('description')}>
                                    Description <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('state')}>
                                    State <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('anonymous')}>
                                    Anonymous <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('date')}>
                                    Date <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('title')}>
                                    Title <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('reporter')}>
                                    Reporter <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('parent')}>
                                    Parent <FontAwesomeIcon icon="sort" />
                                </th>
                                <th />
                            </tr>
                        </thead>
                        <tbody>
                            {reportList.map((report, i) => (
                                <tr key={`entity-${i}`}>
                                    <td>
                                        <Button tag={Link} to={`${match.url}/${report.id}`} color="link" size="sm">
                                            {report.id}
                                        </Button>
                                    </td>
                                    <td>{report.type}</td>
                                    <td>{report.description}</td>
                                    <td>{report.state}</td>
                                    <td>{report.anonymous ? 'true' : 'false'}</td>
                                    <td>
                                        <TextFormat type="date" value={report.date} format={APP_LOCAL_DATE_FORMAT} />
                                    </td>
                                    <td>{report.title}</td>
                                    <td>{report.reporter ? report.reporter.login : ''}</td>
                                    <td>{report.parent ? <Link to={`report/${report.parent.id}`}>{report.parent.id}</Link> : ''}</td>
                                    <td className="text-right">
                                        <div className="btn-group flex-btn-group-container">
                                            <Button tag={Link} to={`${match.url}/${report.id}`} color="info" size="sm">
                                                <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${report.id}/edit`} color="primary" size="sm">
                                                <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${report.id}/delete`} color="danger" size="sm">
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

const mapStateToProps = ({ report }: IRootState) => ({
    reportList: report.entities,
    totalItems: report.totalItems
});

const mapDispatchToProps = {
    getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Report);
