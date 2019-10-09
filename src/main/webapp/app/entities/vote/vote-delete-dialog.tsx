import React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IVote } from 'app/shared/model/vote.model';
import { IRootState } from 'app/shared/reducers';
import { getEntity, deleteEntity } from './vote.reducer';

export interface IVoteDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class VoteDeleteDialog extends React.Component<IVoteDeleteDialogProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    confirmDelete = event => {
        this.props.deleteEntity(this.props.voteEntity.id);
        this.handleClose(event);
    };

    handleClose = event => {
        event.stopPropagation();
        this.props.history.goBack();
    };

    render() {
        const { voteEntity } = this.props;
        return (
            <Modal isOpen toggle={this.handleClose}>
                <ModalHeader toggle={this.handleClose}>Confirm delete operation</ModalHeader>
                <ModalBody id="booghApp.vote.delete.question">Are you sure you want to delete this Vote?</ModalBody>
                <ModalFooter>
                    <Button color="secondary" onClick={this.handleClose}>
                        <FontAwesomeIcon icon="ban" />
                        &nbsp; Cancel
                    </Button>
                    <Button id="jhi-confirm-delete-vote" color="danger" onClick={this.confirmDelete}>
                        <FontAwesomeIcon icon="trash" />
                        &nbsp; Delete
                    </Button>
                </ModalFooter>
            </Modal>
        );
    }
}

const mapStateToProps = ({ vote }: IRootState) => ({
    voteEntity: vote.entity
});

const mapDispatchToProps = { getEntity, deleteEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(VoteDeleteDialog);
