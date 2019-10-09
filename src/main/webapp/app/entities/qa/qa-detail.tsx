import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './qa.reducer';
import { IQA } from 'app/shared/model/qa.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IQADetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class QADetail extends React.Component<IQADetailProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    render() {
        const { qAEntity } = this.props;
        return (
            <Row>
                <Col md="8">
                    <h2>
                        QA [<b>{qAEntity.id}</b>]
                    </h2>
                    <dl className="jh-entity-details">
                        <dt>
                            <span id="question">Question</span>
                        </dt>
                        <dd>{qAEntity.question}</dd>
                        <dt>
                            <span id="answer">Answer</span>
                        </dt>
                        <dd>{qAEntity.answer}</dd>
                        <dt>
                            <span id="order">Order</span>
                        </dt>
                        <dd>{qAEntity.order}</dd>
                    </dl>
                    <Button tag={Link} to="/entity/qa" replace color="info">
                        <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
                    </Button>
                    &nbsp;
                    <Button tag={Link} to={`/entity/qa/${qAEntity.id}/edit`} replace color="primary">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                    </Button>
                </Col>
            </Row>
        );
    }
}

const mapStateToProps = ({ qA }: IRootState) => ({
    qAEntity: qA.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(QADetail);
