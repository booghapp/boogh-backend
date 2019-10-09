import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IReportStatus, defaultValue } from 'app/shared/model/report-status.model';

export const ACTION_TYPES = {
    FETCH_REPORTSTATUS_LIST: 'reportStatus/FETCH_REPORTSTATUS_LIST',
    FETCH_REPORTSTATUS: 'reportStatus/FETCH_REPORTSTATUS',
    CREATE_REPORTSTATUS: 'reportStatus/CREATE_REPORTSTATUS',
    UPDATE_REPORTSTATUS: 'reportStatus/UPDATE_REPORTSTATUS',
    DELETE_REPORTSTATUS: 'reportStatus/DELETE_REPORTSTATUS',
    RESET: 'reportStatus/RESET'
};

const initialState = {
    loading: false,
    errorMessage: null,
    entities: [] as ReadonlyArray<IReportStatus>,
    entity: defaultValue,
    updating: false,
    totalItems: 0,
    updateSuccess: false
};

export type ReportStatusState = Readonly<typeof initialState>;

// Reducer

export default (state: ReportStatusState = initialState, action): ReportStatusState => {
    switch (action.type) {
        case REQUEST(ACTION_TYPES.FETCH_REPORTSTATUS_LIST):
        case REQUEST(ACTION_TYPES.FETCH_REPORTSTATUS):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                loading: true
            };
        case REQUEST(ACTION_TYPES.CREATE_REPORTSTATUS):
        case REQUEST(ACTION_TYPES.UPDATE_REPORTSTATUS):
        case REQUEST(ACTION_TYPES.DELETE_REPORTSTATUS):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                updating: true
            };
        case FAILURE(ACTION_TYPES.FETCH_REPORTSTATUS_LIST):
        case FAILURE(ACTION_TYPES.FETCH_REPORTSTATUS):
        case FAILURE(ACTION_TYPES.CREATE_REPORTSTATUS):
        case FAILURE(ACTION_TYPES.UPDATE_REPORTSTATUS):
        case FAILURE(ACTION_TYPES.DELETE_REPORTSTATUS):
            return {
                ...state,
                loading: false,
                updating: false,
                updateSuccess: false,
                errorMessage: action.payload
            };
        case SUCCESS(ACTION_TYPES.FETCH_REPORTSTATUS_LIST):
            return {
                ...state,
                loading: false,
                totalItems: action.payload.headers['x-total-count'],
                entities: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.FETCH_REPORTSTATUS):
            return {
                ...state,
                loading: false,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.CREATE_REPORTSTATUS):
        case SUCCESS(ACTION_TYPES.UPDATE_REPORTSTATUS):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.DELETE_REPORTSTATUS):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: {}
            };
        case ACTION_TYPES.RESET:
            return {
                ...initialState
            };
        default:
            return state;
    }
};

const apiUrl = 'api/report-statuses';

// Actions

export const getEntities: ICrudGetAllAction<IReportStatus> = (page, size, sort) => {
    const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
    return {
        type: ACTION_TYPES.FETCH_REPORTSTATUS_LIST,
        payload: axios.get<IReportStatus>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
    };
};

export const getEntity: ICrudGetAction<IReportStatus> = id => {
    const requestUrl = `${apiUrl}/${id}`;
    return {
        type: ACTION_TYPES.FETCH_REPORTSTATUS,
        payload: axios.get<IReportStatus>(requestUrl)
    };
};

export const createEntity: ICrudPutAction<IReportStatus> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.CREATE_REPORTSTATUS,
        payload: axios.post(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const updateEntity: ICrudPutAction<IReportStatus> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.UPDATE_REPORTSTATUS,
        payload: axios.put(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const deleteEntity: ICrudDeleteAction<IReportStatus> = id => async dispatch => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await dispatch({
        type: ACTION_TYPES.DELETE_REPORTSTATUS,
        payload: axios.delete(requestUrl)
    });
    dispatch(getEntities());
    return result;
};

export const reset = () => ({
    type: ACTION_TYPES.RESET
});
