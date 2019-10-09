import React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ITelegramChat } from 'app/shared/model/telegram-chat.model';
import { IRootState } from 'app/shared/reducers';
import { getEntity, deleteEntity } from './telegram-chat.reducer';

export interface ITelegramChatDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class TelegramChatDeleteDialog extends React.Component<ITelegramChatDeleteDialogProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    confirmDelete = event => {
        this.props.deleteEntity(this.props.telegramChatEntity.id);
        this.handleClose(event);
    };

    handleClose = event => {
        event.stopPropagation();
        this.props.history.goBack();
    };

    render() {
        const { telegramChatEntity } = this.props;
        return (
            <Modal isOpen toggle={this.handleClose}>
                <ModalHeader toggle={this.handleClose}>Confirm delete operation</ModalHeader>
                <ModalBody id="booghApp.telegramChat.delete.question">Are you sure you want to delete this TelegramChat?</ModalBody>
                <ModalFooter>
                    <Button color="secondary" onClick={this.handleClose}>
                        <FontAwesomeIcon icon="ban" />
                        &nbsp; Cancel
                    </Button>
                    <Button id="jhi-confirm-delete-telegramChat" color="danger" onClick={this.confirmDelete}>
                        <FontAwesomeIcon icon="trash" />
                        &nbsp; Delete
                    </Button>
                </ModalFooter>
            </Modal>
        );
    }
}

const mapStateToProps = ({ telegramChat }: IRootState) => ({
    telegramChatEntity: telegramChat.entity
});

const mapDispatchToProps = { getEntity, deleteEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(TelegramChatDeleteDialog);
