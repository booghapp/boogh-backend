import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { IComment } from 'app/shared/model/comment.model';
import { getEntities as getComments } from 'app/entities/comment/comment.reducer';
import { getEntity, updateEntity, createEntity, reset } from './vote.reducer';
import { IVote } from 'app/shared/model/vote.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IVoteUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IVoteUpdateState {
    isNew: boolean;
    voterId: string;
    commentId: string;
}

export class VoteUpdate extends React.Component<IVoteUpdateProps, IVoteUpdateState> {
    constructor(props) {
        super(props);
        this.state = {
            voterId: '0',
            commentId: '0',
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
        this.props.getComments();
    }

    saveEntity = (event, errors, values) => {
        if (errors.length === 0) {
            const { voteEntity } = this.props;
            const entity = {
                ...voteEntity,
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
        this.props.history.push('/entity/vote');
    };

    render() {
        const { voteEntity, users, comments, loading, updating } = this.props;
        const { isNew } = this.state;

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.vote.home.createOrEditLabel">Create or edit a Vote</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : voteEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput id="vote-id" type="text" className="form-control" name="id" required readOnly />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="voteLabel" for="vote">
                                        Vote
                                    </Label>
                                    <AvField id="vote-vote" type="string" className="form-control" name="vote" />
                                </AvGroup>
                                <AvGroup>
                                    <Label for="voter.login">Voter</Label>
                                    <AvInput
                                        id="vote-voter"
                                        type="select"
                                        className="form-control"
                                        name="voter.id"
                                        value={isNew ? users[0] && users[0].id : voteEntity.voter.id}
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
                                    <Label for="comment.id">Comment</Label>
                                    <AvInput
                                        id="vote-comment"
                                        type="select"
                                        className="form-control"
                                        name="comment.id"
                                        value={isNew ? comments[0] && comments[0].id : voteEntity.comment.id}
                                    >
                                        {comments
                                            ? comments.map(otherEntity => (
                                                  <option value={otherEntity.id} key={otherEntity.id}>
                                                      {otherEntity.id}
                                                  </option>
                                              ))
                                            : null}
                                    </AvInput>
                                </AvGroup>
                                <Button tag={Link} id="cancel-save" to="/entity/vote" replace color="info">
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
    comments: storeState.comment.entities,
    voteEntity: storeState.vote.entity,
    loading: storeState.vote.loading,
    updating: storeState.vote.updating,
    updateSuccess: storeState.vote.updateSuccess
});

const mapDispatchToProps = {
    getUsers,
    getComments,
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
)(VoteUpdate);
