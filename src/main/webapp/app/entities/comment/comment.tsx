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
import { getEntities } from './comment.reducer';
import { IComment } from 'app/shared/model/comment.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface ICommentProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type ICommentState = IPaginationBaseState;

export class Comment extends React.Component<ICommentProps, ICommentState> {
    state: ICommentState = {
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
        const { commentList, match, totalItems } = this.props;
        return (
            <div>
                <h2 id="comment-heading">
                    Comments
                    <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
                        <FontAwesomeIcon icon="plus" />
                        &nbsp; Create new Comment
                    </Link>
                </h2>
                <div className="table-responsive">
                    <Table responsive>
                        <thead>
                            <tr>
                                <th className="hand" onClick={this.sort('id')}>
                                    ID <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('content')}>
                                    Content <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('date')}>
                                    Date <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('commenter')}>
                                    Commenter <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('report')}>
                                    Report <FontAwesomeIcon icon="sort" />
                                </th>
                                <th className="hand" onClick={this.sort('parent')}>
                                    Parent <FontAwesomeIcon icon="sort" />
                                </th>
                                <th />
                            </tr>
                        </thead>
                        <tbody>
                            {commentList.map((comment, i) => (
                                <tr key={`entity-${i}`}>
                                    <td>
                                        <Button tag={Link} to={`${match.url}/${comment.id}`} color="link" size="sm">
                                            {comment.id}
                                        </Button>
                                    </td>
                                    <td>{comment.content}</td>
                                    <td>
                                        <TextFormat type="date" value={comment.date} format={APP_LOCAL_DATE_FORMAT} />
                                    </td>
                                    <td>{comment.commenter ? comment.commenter.login : ''}</td>
                                    <td>{comment.report ? <Link to={`report/${comment.report.id}`}>{comment.report.id}</Link> : ''}</td>
                                    <td>{comment.parent ? <Link to={`comment/${comment.parent.id}`}>{comment.parent.id}</Link> : ''}</td>
                                    <td className="text-right">
                                        <div className="btn-group flex-btn-group-container">
                                            <Button tag={Link} to={`${match.url}/${comment.id}`} color="info" size="sm">
                                                <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${comment.id}/edit`} color="primary" size="sm">
                                                <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${comment.id}/delete`} color="danger" size="sm">
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

const mapStateToProps = ({ comment }: IRootState) => ({
    commentList: comment.entities,
    totalItems: comment.totalItems
});

const mapDispatchToProps = {
    getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Comment);
