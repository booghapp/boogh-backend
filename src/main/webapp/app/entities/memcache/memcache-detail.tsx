import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './memcache.reducer';
import { IMemcache } from 'app/shared/model/memcache.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMemcacheDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MemcacheDetail extends React.Component<IMemcacheDetailProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    render() {
        const { memcacheEntity } = this.props;
        return (
            <Row>
                <Col md="8">
                    <h2>
                        Memcache [<b>{memcacheEntity.id}</b>]
                    </h2>
                    <dl className="jh-entity-details">
                        <dt>
                            <span id="hash">Hash</span>
                        </dt>
                        <dd>{memcacheEntity.hash}</dd>
                        <dt>
                            <span id="telegramId">Telegram Id</span>
                        </dt>
                        <dd>{memcacheEntity.telegramId}</dd>
                        <dt>User</dt>
                        <dd>{memcacheEntity.user ? memcacheEntity.user.login : ''}</dd>
                    </dl>
                    <Button tag={Link} to="/entity/memcache" replace color="info">
                        <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
                    </Button>
                    &nbsp;
                    <Button tag={Link} to={`/entity/memcache/${memcacheEntity.id}/edit`} replace color="primary">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                    </Button>
                </Col>
            </Row>
        );
    }
}

const mapStateToProps = ({ memcache }: IRootState) => ({
    memcacheEntity: memcache.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(MemcacheDetail);
