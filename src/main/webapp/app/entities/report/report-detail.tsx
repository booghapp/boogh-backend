import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import GoogleMapReact, { MapOptions, Maps } from 'google-map-react';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEnvironmentConfiguration, getEntity } from './report.reducer';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IReportDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IReportDetailState {
    googleMapsApiKey: string;
    s3Location: string;
}

export class ReportDetail extends React.Component<IReportDetailProps, IReportDetailState> {
    constructor(props: IReportDetailProps) {
        super(props);
        this.state = { googleMapsApiKey: '', s3Location: '' };
    }

    componentDidMount() {
        this.props.getEntity(this.props.match.params.id);
        getEnvironmentConfiguration(1)['payload'].then(payload => {
            this.setState({ googleMapsApiKey: payload.data[0], s3Location: payload.data[1] });
        });
    }

    spliceReportImages(images) {
        if (images !== undefined && images !== null) {
            const newImages = [];
            for (let i = 0; i < images.length; i += 3) {
                newImages.push(images[i]);
            }
            return newImages;
        }
        return [];
    }

    render() {
        const { reportEntity } = this.props;
        const { googleMapsApiKey, s3Location } = this.state;

        const center = { lat: reportEntity.latitude, lng: reportEntity.longitude };

        let googleMap = <div />;
        if (googleMapsApiKey !== '') {
            googleMap = (
                <GoogleMapReact bootstrapURLKeys={{ key: googleMapsApiKey }} center={center} zoom={12} height={500} width={500}>
                    <Marker lat={reportEntity.latitude} lng={reportEntity.longitude} />
                </GoogleMapReact>
            );
        }
        let imagesJSX = [];
        const { images } = reportEntity;

        const newImages = this.spliceReportImages(images);

        if (s3Location !== '') {
            imagesJSX =
                newImages !== undefined && newImages !== null
                    ? newImages.map(image => (
                          <div key={image} style={{ margin: 10 }}>
                              <img src={this.state.s3Location + image} />
                          </div>
                      ))
                    : null;
        }

        return (
            <Row>
                <Col md="8">
                    <h2>
                        Report [<b>{reportEntity.id}</b>]
                    </h2>
                    <dl className="jh-entity-details">
                        <dt>
                            <span id="type">Type</span>
                        </dt>
                        <dd>{reportEntity.type}</dd>
                        <dt>
                            <span id="description">Description</span>
                        </dt>
                        <dd style={{ direction: 'rtl', textAlign: 'right' }}>{reportEntity.description}</dd>
                        <dt>
                            <span id="state">State</span>
                        </dt>
                        <dd>{reportEntity.state}</dd>
                        <dt>
                            <span id="anonymous">Anonymous</span>
                        </dt>
                        <dd>{reportEntity.anonymous ? 'true' : 'false'}</dd>
                        <dt>
                            <span id="latitude">Latitude</span>
                        </dt>
                        <dd>{reportEntity.latitude}</dd>
                        <dt>
                            <span id="longitude">Longitude</span>
                        </dt>
                        <dd>{reportEntity.longitude}</dd>
                        <dt>
                            <span id="date">Date</span>
                        </dt>
                        <dd>
                            <TextFormat value={reportEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} />
                        </dd>
                        <dt>
                            <span id="title">Title</span>
                        </dt>
                        <dd style={{ direction: 'rtl' }}>{reportEntity.title}</dd>
                        <dt>Reporter</dt>
                        <dd>{reportEntity.reporter ? reportEntity.reporter.login : ''}</dd>
                        <dt>Parent</dt>
                        <dd>{reportEntity.parent ? reportEntity.parent.id : ''}</dd>
                        <dt>Attachments</dt>
                        <dd>{imagesJSX}</dd>
                        <dt>Location</dt>
                    </dl>
                    <div style={{ display: 'block', height: 500, width: 500 }}>{googleMap}</div>
                    <Button tag={Link} to="/entity/report" replace color="info">
                        <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
                    </Button>
                    &nbsp;
                    <Button tag={Link} to={`/entity/report/${reportEntity.id}/edit`} replace color="primary">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                    </Button>
                </Col>
            </Row>
        );
    }
}

interface IMarkerProps {
    lat: number;
    lng: number;
}

function Marker(props: IMarkerProps) {
    return <div style={{ height: 12, width: 12, backgroundColor: 'blue', borderRadius: '50%' }} />;
}

const mapStateToProps = ({ report }: IRootState) => ({
    reportEntity: report.entity
});

const mapDispatchToProps = { getEntity, getEnvironmentConfiguration };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ReportDetail);
