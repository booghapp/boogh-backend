import React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IDocument } from 'app/shared/model/document.model';
import { IRootState } from 'app/shared/reducers';
import { getEntity, deleteEntity } from './document.reducer';

export interface IDocumentDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class DocumentDeleteDialog extends React.Component<IDocumentDeleteDialogProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    confirmDelete = event => {
        this.props.deleteEntity(this.props.documentEntity.id);
        this.handleClose(event);
    };

    handleClose = event => {
        event.stopPropagation();
        this.props.history.goBack();
    };

    render() {
        const { documentEntity } = this.props;
        return (
            <Modal isOpen toggle={this.handleClose}>
                <ModalHeader toggle={this.handleClose}>Confirm delete operation</ModalHeader>
                <ModalBody id="booghApp.document.delete.question">Are you sure you want to delete this Document?</ModalBody>
                <ModalFooter>
                    <Button color="secondary" onClick={this.handleClose}>
                        <FontAwesomeIcon icon="ban" />
                        &nbsp; Cancel
                    </Button>
                    <Button id="jhi-confirm-delete-document" color="danger" onClick={this.confirmDelete}>
                        <FontAwesomeIcon icon="trash" />
                        &nbsp; Delete
                    </Button>
                </ModalFooter>
            </Modal>
        );
    }
}

const mapStateToProps = ({ document }: IRootState) => ({
    documentEntity: document.entity
});

const mapDispatchToProps = { getEntity, deleteEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(DocumentDeleteDialog);
