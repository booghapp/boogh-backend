import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, setBlob, reset } from './feedback.reducer';
import { IFeedback } from 'app/shared/model/feedback.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IFeedbackUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IFeedbackUpdateState {
    isNew: boolean;
}

export class FeedbackUpdate extends React.Component<IFeedbackUpdateProps, IFeedbackUpdateState> {
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

    onBlobChange = (isAnImage, name) => event => {
        setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
    };

    clearBlob = name => () => {
        this.props.setBlob(name, undefined, undefined);
    };

    saveEntity = (event, errors, values) => {
        if (errors.length === 0) {
            const { feedbackEntity } = this.props;
            const entity = {
                ...feedbackEntity,
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
        this.props.history.push('/entity/feedback');
    };

    render() {
        const { feedbackEntity, loading, updating } = this.props;
        const { isNew } = this.state;

        const { content } = feedbackEntity;

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.feedback.home.createOrEditLabel">Create or edit a Feedback</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : feedbackEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput id="feedback-id" type="text" className="form-control" name="id" required readOnly />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="contentLabel" for="content">
                                        Content
                                    </Label>
                                    <AvInput
                                        id="feedback-content"
                                        type="textarea"
                                        name="content"
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' }
                                        }}
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="createdOnLabel" for="createdOn">
                                        Created On
                                    </Label>
                                    <AvField id="feedback-createdOn" type="date" className="form-control" name="createdOn" />
                                </AvGroup>
                                <Button tag={Link} id="cancel-save" to="/entity/feedback" replace color="info">
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
    feedbackEntity: storeState.feedback.entity,
    loading: storeState.feedback.loading,
    updating: storeState.feedback.updating,
    updateSuccess: storeState.feedback.updateSuccess
});

const mapDispatchToProps = {
    getEntity,
    updateEntity,
    setBlob,
    createEntity,
    reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(FeedbackUpdate);
