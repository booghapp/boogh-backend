import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IReport, defaultValue } from 'app/shared/model/report.model';

export const ACTION_TYPES = {
    FETCH_REPORT_LIST: 'report/FETCH_REPORT_LIST',
    FETCH_REPORT: 'report/FETCH_REPORT',
    FETCH_ENVIRONMENT_CONFIGURATION: 'report/FETCH_ENVIRONMENT_CONFIGURATION',
    CREATE_REPORT: 'report/CREATE_REPORT',
    UPDATE_REPORT: 'report/UPDATE_REPORT',
    DELETE_REPORT: 'report/DELETE_REPORT',
    SET_BLOB: 'report/SET_BLOB',
    RESET: 'report/RESET'
};

const initialState = {
    loading: false,
    errorMessage: null,
    entities: [] as ReadonlyArray<IReport>,
    entity: defaultValue,
    updating: false,
    totalItems: 0,
    updateSuccess: false
};

export type ReportState = Readonly<typeof initialState>;

export type EnvironmentState = Readonly<typeof initialState>;

// Reducer

export default (state: ReportState = initialState, action): ReportState => {
    switch (action.type) {
        case REQUEST(ACTION_TYPES.FETCH_REPORT_LIST):
        case REQUEST(ACTION_TYPES.FETCH_REPORT):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                loading: true
            };
        case REQUEST(ACTION_TYPES.CREATE_REPORT):
        case REQUEST(ACTION_TYPES.UPDATE_REPORT):
        case REQUEST(ACTION_TYPES.DELETE_REPORT):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                updating: true
            };
        case FAILURE(ACTION_TYPES.FETCH_REPORT_LIST):
        case FAILURE(ACTION_TYPES.FETCH_REPORT):
        case FAILURE(ACTION_TYPES.CREATE_REPORT):
        case FAILURE(ACTION_TYPES.UPDATE_REPORT):
        case FAILURE(ACTION_TYPES.DELETE_REPORT):
            return {
                ...state,
                loading: false,
                updating: false,
                updateSuccess: false,
                errorMessage: action.payload
            };
        case SUCCESS(ACTION_TYPES.FETCH_REPORT_LIST):
            return {
                ...state,
                loading: false,
                totalItems: action.payload.headers['x-total-count'],
                entities: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.FETCH_REPORT):
            return {
                ...state,
                loading: false,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.CREATE_REPORT):
        case SUCCESS(ACTION_TYPES.UPDATE_REPORT):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.DELETE_REPORT):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: {}
            };
        case ACTION_TYPES.SET_BLOB:
            const { name, data, contentType } = action.payload;
            return {
                ...state,
                entity: {
                    ...state.entity,
                    [name]: data,
                    [name + 'ContentType']: contentType
                }
            };
        case ACTION_TYPES.RESET:
            return {
                ...initialState
            };
        default:
            return state;
    }
};

const apiUrl = 'api/reports';
const envConfigUrl = 'api/configuration';

// Actions

export const getEntities: ICrudGetAllAction<IReport> = (page, size, sort) => {
    const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
    return {
        type: ACTION_TYPES.FETCH_REPORT_LIST,
        payload: axios.get<IReport>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
    };
};

export const getEntity: ICrudGetAction<IReport> = id => {
    const requestUrl = `${apiUrl}/${id}`;
    return {
        type: ACTION_TYPES.FETCH_REPORT,
        payload: axios.get<IReport>(requestUrl)
    };
};

export const getEnvironmentConfiguration: ICrudGetAction<any> = () => {
    const requestUrl = `${envConfigUrl}`;
    return {
        type: ACTION_TYPES.FETCH_ENVIRONMENT_CONFIGURATION,
        payload: axios.get<any>(requestUrl)
    };
};

export const createEntity: ICrudPutAction<IReport> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.CREATE_REPORT,
        payload: axios.post(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const updateEntity: ICrudPutAction<IReport> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.UPDATE_REPORT,
        payload: axios.put(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const deleteEntity: ICrudDeleteAction<IReport> = id => async dispatch => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await dispatch({
        type: ACTION_TYPES.DELETE_REPORT,
        payload: axios.delete(requestUrl)
    });
    dispatch(getEntities());
    return result;
};

export const setBlob = (name, data, contentType?) => ({
    type: ACTION_TYPES.SET_BLOB,
    payload: {
        name,
        data,
        contentType
    }
});

export const reset = () => ({
    type: ACTION_TYPES.RESET
});
