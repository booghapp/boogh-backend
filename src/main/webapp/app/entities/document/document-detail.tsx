import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './document.reducer';
import { IDocument } from 'app/shared/model/document.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IDocumentDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class DocumentDetail extends React.Component<IDocumentDetailProps> {
    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
    }

    render() {
        const { documentEntity } = this.props;
        return (
            <Row>
                <Col md="8">
                    <h2>
                        Document [<b>{documentEntity.id}</b>]
                    </h2>
                    <dl className="jh-entity-details">
                        <dt>
                            <span id="type">Type</span>
                        </dt>
                        <dd>{documentEntity.type}</dd>
                        <dt>
                            <span id="content">Content</span>
                        </dt>
                        <dd>{documentEntity.content}</dd>
                    </dl>
                    <Button tag={Link} to="/entity/document" replace color="info">
                        <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
                    </Button>
                    &nbsp;
                    <Button tag={Link} to={`/entity/document/${documentEntity.id}/edit`} replace color="primary">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                    </Button>
                </Col>
            </Row>
        );
    }
}

const mapStateToProps = ({ document }: IRootState) => ({
    documentEntity: document.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(DocumentDetail);
