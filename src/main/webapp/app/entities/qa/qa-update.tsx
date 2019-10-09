import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './qa.reducer';
import { IQA } from 'app/shared/model/qa.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IQAUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IQAUpdateState {
    isNew: boolean;
}

export class QAUpdate extends React.Component<IQAUpdateProps, IQAUpdateState> {
    constructor(props) {
        super(props);
        this.state = {
            isNew: !this.props.match.params || !this.props.match.params.id
        };
    }

    componentWillUpdate(nextProps, nextState) {
        if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
            this.handleClose();
        }
    }

    componentDidMount() {
        if (this.state.isNew) {
            this.props.reset();
        } else {
            this.props.getEntity(this.props.match.params.id);
        }
    }

    saveEntity = (event, errors, values) => {
        if (errors.length === 0) {
            const { qAEntity } = this.props;
            const entity = {
                ...qAEntity,
                ...values
            };

            if (this.state.isNew) {
                this.props.createEntity(entity);
            } else {
                this.props.updateEntity(entity);
            }
        }
    };

    handleClose = () => {
        this.props.history.push('/entity/qa');
    };

    render() {
        const { qAEntity, loading, updating } = this.props;
        const { isNew } = this.state;

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.qA.home.createOrEditLabel">Create or edit a QA</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : qAEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput id="qa-id" type="text" className="form-control" name="id" required readOnly />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="questionLabel" for="question">
                                        Question
                                    </Label>
                                    <AvField
                                        id="qa-question"
                                        type="text"
                                        name="question"
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' }
                                        }}
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="answerLabel" for="answer">
                                        Answer
                                    </Label>
                                    <AvField
                                        id="qa-answer"
                                        type="text"
                                        name="answer"
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' }
                                        }}
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="orderLabel" for="order">
                                        Order
                                    </Label>
                                    <AvField
                                        id="qa-order"
                                        type="string"
                                        className="form-control"
                                        name="order"
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' },
                                            number: { value: true, errorMessage: 'This field should be a number.' }
                                        }}
                                    />
                                </AvGroup>
                                <Button tag={Link} id="cancel-save" to="/entity/qa" replace color="info">
                                    <FontAwesomeIcon icon="arrow-left" />
                                    &nbsp;
                                    <span className="d-none d-md-inline">Back</span>
                                </Button>
                                &nbsp;
                                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                                    <FontAwesomeIcon icon="save" />
                                    &nbsp; Save
                                </Button>
                            </AvForm>
                        )}
                    </Col>
                </Row>
            </div>
        );
    }
}

const mapStateToProps = (storeState: IRootState) => ({
    qAEntity: storeState.qA.entity,
    loading: storeState.qA.loading,
    updating: storeState.qA.updating,
    updateSuccess: storeState.qA.updateSuccess
});

const mapDispatchToProps = {
    getEntity,
    updateEntity,
    createEntity,
    reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(QAUpdate);
