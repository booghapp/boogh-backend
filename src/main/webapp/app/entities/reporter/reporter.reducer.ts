import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IReporter, defaultValue } from 'app/shared/model/reporter.model';

export const ACTION_TYPES = {
    FETCH_REPORTER_LIST: 'reporter/FETCH_REPORTER_LIST',
    FETCH_REPORTER: 'reporter/FETCH_REPORTER',
    CREATE_REPORTER: 'reporter/CREATE_REPORTER',
    UPDATE_REPORTER: 'reporter/UPDATE_REPORTER',
    DELETE_REPORTER: 'reporter/DELETE_REPORTER',
    SET_BLOB: 'reporter/SET_BLOB',
    RESET: 'reporter/RESET'
};

const initialState = {
    loading: false,
    errorMessage: null,
    entities: [] as ReadonlyArray<IReporter>,
    entity: defaultValue,
    updating: false,
    totalItems: 0,
    updateSuccess: false
};

export type ReporterState = Readonly<typeof initialState>;

// Reducer

export default (state: ReporterState = initialState, action): ReporterState => {
    switch (action.type) {
        case REQUEST(ACTION_TYPES.FETCH_REPORTER_LIST):
        case REQUEST(ACTION_TYPES.FETCH_REPORTER):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                loading: true
            };
        case REQUEST(ACTION_TYPES.CREATE_REPORTER):
        case REQUEST(ACTION_TYPES.UPDATE_REPORTER):
        case REQUEST(ACTION_TYPES.DELETE_REPORTER):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                updating: true
            };
        case FAILURE(ACTION_TYPES.FETCH_REPORTER_LIST):
        case FAILURE(ACTION_TYPES.FETCH_REPORTER):
        case FAILURE(ACTION_TYPES.CREATE_REPORTER):
        case FAILURE(ACTION_TYPES.UPDATE_REPORTER):
        case FAILURE(ACTION_TYPES.DELETE_REPORTER):
            return {
                ...state,
                loading: false,
                updating: false,
                updateSuccess: false,
                errorMessage: action.payload
            };
        case SUCCESS(ACTION_TYPES.FETCH_REPORTER_LIST):
            return {
                ...state,
                loading: false,
                totalItems: action.payload.headers['x-total-count'],
                entities: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.FETCH_REPORTER):
            return {
                ...state,
                loading: false,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.CREATE_REPORTER):
        case SUCCESS(ACTION_TYPES.UPDATE_REPORTER):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.DELETE_REPORTER):
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

const apiUrl = 'api/reporters';

// Actions

export const getEntities: ICrudGetAllAction<IReporter> = (page, size, sort) => {
    const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
    return {
        type: ACTION_TYPES.FETCH_REPORTER_LIST,
        payload: axios.get<IReporter>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
    };
};

export const getEntity: ICrudGetAction<IReporter> = id => {
    const requestUrl = `${apiUrl}/${id}`;
    return {
        type: ACTION_TYPES.FETCH_REPORTER,
        payload: axios.get<IReporter>(requestUrl)
    };
};

export const createEntity: ICrudPutAction<IReporter> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.CREATE_REPORTER,
        payload: axios.post(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const updateEntity: ICrudPutAction<IReporter> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.UPDATE_REPORTER,
        payload: axios.put(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const deleteEntity: ICrudDeleteAction<IReporter> = id => async dispatch => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await dispatch({
        type: ACTION_TYPES.DELETE_REPORTER,
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
