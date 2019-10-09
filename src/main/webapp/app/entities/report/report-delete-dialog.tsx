import React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IReport } from 'app/shared/model/report.model';
import { IRootState } from 'app/shared/reducers';
import { getEntity, deleteEntity } from './report.reducer';

export interface IReportDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ReportDeleteDialog extends React.Component<IReportDeleteDialogProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    confirmDelete = event => {
        this.props.deleteEntity(this.props.reportEntity.id);
        this.handleClose(event);
    };

    handleClose = event => {
        event.stopPropagation();
        this.props.history.goBack();
    };

    render() {
        const { reportEntity } = this.props;
        return (
            <Modal isOpen toggle={this.handleClose}>
                <ModalHeader toggle={this.handleClose}>Confirm delete operation</ModalHeader>
                <ModalBody id="booghApp.report.delete.question">Are you sure you want to delete this Report?</ModalBody>
                <ModalFooter>
                    <Button color="secondary" onClick={this.handleClose}>
                        <FontAwesomeIcon icon="ban" />
                        &nbsp; Cancel
                    </Button>
                    <Button id="jhi-confirm-delete-report" color="danger" onClick={this.confirmDelete}>
                        <FontAwesomeIcon icon="trash" />
                        &nbsp; Delete
                    </Button>
                </ModalFooter>
            </Modal>
        );
    }
}

const mapStateToProps = ({ report }: IRootState) => ({
    reportEntity: report.entity
});

const mapDispatchToProps = { getEntity, deleteEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ReportDeleteDialog);
