import React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IMemcache } from 'app/shared/model/memcache.model';
import { IRootState } from 'app/shared/reducers';
import { getEntity, deleteEntity } from './memcache.reducer';

export interface IMemcacheDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MemcacheDeleteDialog extends React.Component<IMemcacheDeleteDialogProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    confirmDelete = event => {
        this.props.deleteEntity(this.props.memcacheEntity.id);
        this.handleClose(event);
    };

    handleClose = event => {
        event.stopPropagation();
        this.props.history.goBack();
    };

    render() {
        const { memcacheEntity } = this.props;
        return (
            <Modal isOpen toggle={this.handleClose}>
                <ModalHeader toggle={this.handleClose}>Confirm delete operation</ModalHeader>
                <ModalBody id="booghApp.memcache.delete.question">Are you sure you want to delete this Memcache?</ModalBody>
                <ModalFooter>
                    <Button color="secondary" onClick={this.handleClose}>
                        <FontAwesomeIcon icon="ban" />
                        &nbsp; Cancel
                    </Button>
                    <Button id="jhi-confirm-delete-memcache" color="danger" onClick={this.confirmDelete}>
                        <FontAwesomeIcon icon="trash" />
                        &nbsp; Delete
                    </Button>
                </ModalFooter>
            </Modal>
        );
    }
}

const mapStateToProps = ({ memcache }: IRootState) => ({
    memcacheEntity: memcache.entity
});

const mapDispatchToProps = { getEntity, deleteEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(MemcacheDeleteDialog);
