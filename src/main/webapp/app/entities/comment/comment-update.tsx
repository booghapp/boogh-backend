import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { IReport } from 'app/shared/model/report.model';
import { getEntities as getReports } from 'app/entities/report/report.reducer';
import { getEntities as getComments } from 'app/entities/comment/comment.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './comment.reducer';
import { IComment } from 'app/shared/model/comment.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ICommentUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface ICommentUpdateState {
    isNew: boolean;
    commenterId: string;
    reportId: string;
    parentId: string;
}

export class CommentUpdate extends React.Component<ICommentUpdateProps, ICommentUpdateState> {
    constructor(props) {
        super(props);
        this.state = {
            commenterId: '0',
            reportId: '0',
            parentId: '0',
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

        this.props.getUsers();
        this.props.getReports();
        this.props.getComments();
    }

    onBlobChange = (isAnImage, name) => event => {
        setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
    };

    clearBlob = name => () => {
        this.props.setBlob(name, undefined, undefined);
    };

    saveEntity = (event, errors, values) => {
        if (errors.length === 0) {
            const { commentEntity } = this.props;
            const entity = {
                ...commentEntity,
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
        this.props.history.push('/entity/comment');
    };

    render() {
        const { commentEntity, users, reports, comments, loading, updating } = this.props;
        const { isNew } = this.state;

        const { content } = commentEntity;

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.comment.home.createOrEditLabel">Create or edit a Comment</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : commentEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput id="comment-id" type="text" className="form-control" name="id" required readOnly />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="contentLabel" for="content">
                                        Content
                                    </Label>
                                    <AvInput
                                        id="comment-content"
                                        type="textarea"
                                        name="content"
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' }
                                        }}
                                    />
                                </AvGroup>
                                <AvGroup>
                                    <Label id="dateLabel" for="date">
                                        Date
                                    </Label>
                                    <AvField id="comment-date" type="date" className="form-control" name="date" />
                                </AvGroup>
                                <AvGroup>
                                    <Label for="commenter.login">Commenter</Label>
                                    <AvInput
                                        id="comment-commenter"
                                        type="select"
                                        className="form-control"
                                        name="commenter.id"
                                        value={isNew ? users[0] && users[0].id : commentEntity.commenter.id}
                                    >
                                        {users
                                            ? users.map(otherEntity => (
                                                  <option value={otherEntity.id} key={otherEntity.id}>
                                                      {otherEntity.login}
                                                  </option>
                                              ))
                                            : null}
                                    </AvInput>
                                </AvGroup>
                                <AvGroup>
                                    <Label for="report.id">Report</Label>
                                    <AvInput
                                        id="comment-report"
                                        type="select"
                                        className="form-control"
                                        name="report.id"
                                        value={isNew ? reports[0] && reports[0].id : commentEntity.report.id}
                                    >
                                        {reports
                                            ? reports.map(otherEntity => (
                                                  <option value={otherEntity.id} key={otherEntity.id}>
                                                      {otherEntity.id}
                                                  </option>
                                              ))
                                            : null}
                                    </AvInput>
                                </AvGroup>
                                <AvGroup>
                                    <Label for="parent.id">Parent</Label>
                                    <AvInput id="comment-parent" type="select" className="form-control" name="parent.id">
                                        <option value="" key="0" />
                                        {comments
                                            ? comments.map(otherEntity => (
                                                  <option value={otherEntity.id} key={otherEntity.id}>
                                                      {otherEntity.id}
                                                  </option>
                                              ))
                                            : null}
                                    </AvInput>
                                </AvGroup>
                                <Button tag={Link} id="cancel-save" to="/entity/comment" replace color="info">
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
    users: storeState.userManagement.users,
    reports: storeState.report.entities,
    comments: storeState.comment.entities,
    commentEntity: storeState.comment.entity,
    loading: storeState.comment.loading,
    updating: storeState.comment.updating,
    updateSuccess: storeState.comment.updateSuccess
});

const mapDispatchToProps = {
    getUsers,
    getReports,
    getComments,
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
)(CommentUpdate);
