import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAllAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './memcache.reducer';
import { IMemcache } from 'app/shared/model/memcache.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMemcacheProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export class Memcache extends React.Component<IMemcacheProps> {
    componentDidMount() {
        this.props.getEntities();
    }

    render() {
        const { memcacheList, match } = this.props;
        return (
            <div>
                <h2 id="memcache-heading">
                    Memcaches
                    <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
                        <FontAwesomeIcon icon="plus" />
                        &nbsp; Create new Memcache
                    </Link>
                </h2>
                <div className="table-responsive">
                    <Table responsive>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Hash</th>
                                <th>Telegram Id</th>
                                <th>User</th>
                                <th />
                            </tr>
                        </thead>
                        <tbody>
                            {memcacheList.map((memcache, i) => (
                                <tr key={`entity-${i}`}>
                                    <td>
                                        <Button tag={Link} to={`${match.url}/${memcache.id}`} color="link" size="sm">
                                            {memcache.id}
                                        </Button>
                                    </td>
                                    <td>{memcache.hash}</td>
                                    <td>{memcache.telegramId}</td>
                                    <td>{memcache.user ? memcache.user.login : ''}</td>
                                    <td className="text-right">
                                        <div className="btn-group flex-btn-group-container">
                                            <Button tag={Link} to={`${match.url}/${memcache.id}`} color="info" size="sm">
                                                <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${memcache.id}/edit`} color="primary" size="sm">
                                                <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                                            </Button>
                                            <Button tag={Link} to={`${match.url}/${memcache.id}/delete`} color="danger" size="sm">
                                                <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                                            </Button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </div>
            </div>
        );
    }
}

const mapStateToProps = ({ memcache }: IRootState) => ({
    memcacheList: memcache.entities
});

const mapDispatchToProps = {
    getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Memcache);
