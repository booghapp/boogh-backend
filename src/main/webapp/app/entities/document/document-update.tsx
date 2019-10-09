import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import * as Showdown from 'showdown';
import ReactMde from 'react-mde';
import 'react-mde/lib/styles/css/react-mde-all.css';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, setFileData, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, setBlob, reset } from './document.reducer';
import { IDocument } from 'app/shared/model/document.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IDocumentUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IDocumentUpdateState {
    isNew: boolean;
    value: string;
    tabSelected: 'write' | 'preview';
    prevContent: string;
}

export class DocumentUpdate extends React.Component<IDocumentUpdateProps, IDocumentUpdateState> {
    converter;
    constructor(props) {
        super(props);
        this.state = {
            isNew: !this.props.match.params || !this.props.match.params.id,
            value: '',
            tabSelected: 'write',
            prevContent: ''
        };
        this.converter = new Showdown.Converter({
            tables: true,
            simplifiedAutoLink: true,
            strikethrough: true,
            tasklists: true
        });
        this.handleValueChange = this.handleValueChange.bind(this);
        this.handleTabChange = this.handleTabChange.bind(this);
        this.resolvePromise = this.resolvePromise.bind(this);
    }

    componentWillUpdate(nextProps, nextState) {
        if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
            this.handleClose();
        }
    }

    componentDidMount() {
        if (this.state.isNew) {
            this.props.reset();
            this.setState({ value: '' });
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
            const { documentEntity } = this.props;
            const entity = {
                ...documentEntity,
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
        this.props.history.push('/entity/document');
    };

    handleValueChange = value => {
        this.setState({ value });
    };

    handleTabChange() {
        if (this.state.tabSelected === 'write') {
            this.setState({ tabSelected: 'preview' });
        } else {
            this.setState({ tabSelected: 'write' });
        }
    }

    resolvePromise(markdown) {
        return Promise.resolve(this.converter.makeHtml(markdown));
    }

    render() {
        const { documentEntity, loading, updating } = this.props;
        const { isNew } = this.state;

        const { content } = documentEntity;
        if (content !== this.state.prevContent) {
            this.setState({ value: content, prevContent: content });
        }

        return (
            <div>
                <Row className="justify-content-center">
                    <Col md="8">
                        <h2 id="booghApp.document.home.createOrEditLabel">Create or edit a Document</h2>
                    </Col>
                </Row>
                <Row className="justify-content-center">
                    <Col md="8">
                        {loading ? (
                            <p>Loading...</p>
                        ) : (
                            <AvForm model={isNew ? {} : documentEntity} onSubmit={this.saveEntity}>
                                {!isNew ? (
                                    <AvGroup>
                                        <Label for="id">ID</Label>
                                        <AvInput id="document-id" type="text" className="form-control" name="id" required readOnly />
                                    </AvGroup>
                                ) : null}
                                <AvGroup>
                                    <Label id="typeLabel">Type</Label>
                                    <AvInput
                                        id="document-type"
                                        type="select"
                                        className="form-control"
                                        name="type"
                                        value={(!isNew && documentEntity.type) || 'ABOUT'}
                                    >
                                        <option value="ABOUT">ABOUT</option>
                                        <option value="TERMSOFUSE">TERMSOFUSE</option>
                                        <option value="PRIVACYPOLICY">PRIVACYPOLICY</option>
                                    </AvInput>
                                </AvGroup>
                                <AvGroup>
                                    <Label id="contentLabel" for="content">
                                        Content
                                    </Label>
                                    <AvInput
                                        value={this.state.value}
                                        id="document-content"
                                        type="textarea"
                                        name="content"
                                        validate={{
                                            required: { value: true, errorMessage: 'This field is required.' }
                                        }}
                                    />
                                </AvGroup>
                                <div className="container">
                                    <ReactMde
                                        onChange={this.handleValueChange}
                                        selectedTab={this.state.tabSelected}
                                        onTabChange={this.handleTabChange}
                                        value={this.state.value}
                                        generateMarkdownPreview={this.resolvePromise}
                                    />
                                </div>
                                <Button tag={Link} id="cancel-save" to="/entity/document" replace color="info">
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
    documentEntity: storeState.document.entity,
    loading: storeState.document.loading,
    updating: storeState.document.updating,
    updateSuccess: storeState.document.updateSuccess
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
)(DocumentUpdate);
