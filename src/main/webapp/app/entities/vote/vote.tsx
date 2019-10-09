import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAllAction, getSortState, IPaginationBaseState, getPaginationItemsNumber, JhiPagination } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './vote.reducer';
import { IVote } from 'app/shared/model/vote.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface IVoteProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type IVoteState = IPaginationBaseState;

export class Vote extends React.Component<IVoteProps, IVoteState> {
    state: IVoteState = {
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
        const { voteList, match, totalItems } = this.props;
        return (
            <div>
                <h2 id="vote-heading">
                    Votes
                    <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
                        <FontAwesomeIcon icon="plus" />
                        &nbsp; Create new Vote
                    </Link>
                </h2>
                <div className="table-responsive">
                    <Table responsive>
                        <thead>
                            <tr>
                                <th className="hand" onClick={this.sort('id')}>
                                    ID <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('vote')}>
                                    Vote <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('voter')}>
                                    Voter <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('comment')}>
                                    Comment <FontAwesomeIcon icon="sort" />
                                </th>
                                <th />
                            </tr>
                        </thead>
                        <tbody>
                            {voteList.map((vote, i) => (
                                <tr key={`entity-${i}`}>
                                    <td>
                                        <Button tag={Link} to={`${match.url}/${vote.id}`} color="link" size="sm">
                                            {vote.id}
                                        </Button>
                                    </td>
                                    <td>{vote.vote}</td>
                                    <td>{vote.voter ? vote.voter.login : ''}</td>
                                    <td>{vote.comment ? <Link to={`comment/${vote.comment.id}`}>{vote.comment.id}</Link> : ''}</td>
                                    <td className="text-right">
                                        <div className="btn-group flex-btn-group-container">
                                            <Button tag={Link} to={`${match.url}/${vote.id}`} color="info" size="sm">
                                                <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${vote.id}/edit`} color="primary" size="sm">
                                                <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${vote.id}/delete`} color="danger" size="sm">
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

const mapStateToProps = ({ vote }: IRootState) => ({
    voteList: vote.entities,
    totalItems: vote.totalItems
});

const mapDispatchToProps = {
    getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Vote);
