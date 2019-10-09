import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { byteSize, ICrudGetAllAction, getSortState, IPaginationBaseState, getPaginationItemsNumber, JhiPagination } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './reporter.reducer';
import { IReporter } from 'app/shared/model/reporter.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface IReporterProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type IReporterState = IPaginationBaseState;

export class Reporter extends React.Component<IReporterProps, IReporterState> {
    state: IReporterState = {
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
        const { reporterList, match, totalItems } = this.props;
        return (
            <div>
                <h2 id="reporter-heading">
                    Reporters
                    <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
                        <FontAwesomeIcon icon="plus" />
                        &nbsp; Create new Reporter
                    </Link>
                </h2>
                <div className="table-responsive">
                    <Table responsive>
                        <thead>
                            <tr>
                                <th className="hand" onClick={this.sort('id')}>
                                    ID <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('about')}>
                                    About <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('karma')}>
                                    Karma <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('visibility')}>
                                    Visibility <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('moderator')}>
                                    Moderator <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('location')}>
                                    Location <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('notificationsOn')}>
                                    Notifications On <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('user')}>
                                    User <FontAwesomeIcon icon="sort" />
                                </th>
                                <th />
                            </tr>
                        </thead>
                        <tbody>
                            {reporterList.map((reporter, i) => (
                                <tr key={`entity-${i}`}>
                                    <td>
                                        <Button tag={Link} to={`${match.url}/${reporter.id}`} color="link" size="sm">
                                            {reporter.id}
                                        </Button>
                                    </td>
                                    <td>{reporter.about}</td>
                                    <td>{reporter.karma}</td>
                                    <td>{reporter.visibility ? 'true' : 'false'}</td>
                                    <td>{reporter.moderator ? 'true' : 'false'}</td>
                                    <td>{reporter.location}</td>
                                    <td>{reporter.notificationsOn ? 'true' : 'false'}</td>
                                    <td>{reporter.user ? reporter.user.id : ''}</td>
                                    <td className="text-right">
                                        <div className="btn-group flex-btn-group-container">
                                            <Button tag={Link} to={`${match.url}/${reporter.id}`} color="info" size="sm">
                                                <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${reporter.id}/edit`} color="primary" size="sm">
                                                <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${reporter.id}/delete`} color="danger" size="sm">
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

const mapStateToProps = ({ reporter }: IRootState) => ({
    reporterList: reporter.entities,
    totalItems: reporter.totalItems
});

const mapDispatchToProps = {
    getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Reporter);
