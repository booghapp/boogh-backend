import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IQA, defaultValue } from 'app/shared/model/qa.model';

export const ACTION_TYPES = {
    FETCH_QA_LIST: 'qA/FETCH_QA_LIST',
    FETCH_QA: 'qA/FETCH_QA',
    CREATE_QA: 'qA/CREATE_QA',
    UPDATE_QA: 'qA/UPDATE_QA',
    DELETE_QA: 'qA/DELETE_QA',
    RESET: 'qA/RESET'
};

const initialState = {
    loading: false,
    errorMessage: null,
    entities: [] as ReadonlyArray<IQA>,
    entity: defaultValue,
    updating: false,
    updateSuccess: false
};

export type QAState = Readonly<typeof initialState>;

// Reducer

export default (state: QAState = initialState, action): QAState => {
    switch (action.type) {
        case REQUEST(ACTION_TYPES.FETCH_QA_LIST):
        case REQUEST(ACTION_TYPES.FETCH_QA):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                loading: true
            };
        case REQUEST(ACTION_TYPES.CREATE_QA):
        case REQUEST(ACTION_TYPES.UPDATE_QA):
        case REQUEST(ACTION_TYPES.DELETE_QA):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                updating: true
            };
        case FAILURE(ACTION_TYPES.FETCH_QA_LIST):
        case FAILURE(ACTION_TYPES.FETCH_QA):
        case FAILURE(ACTION_TYPES.CREATE_QA):
        case FAILURE(ACTION_TYPES.UPDATE_QA):
        case FAILURE(ACTION_TYPES.DELETE_QA):
            return {
                ...state,
                loading: false,
                updating: false,
                updateSuccess: false,
                errorMessage: action.payload
            };
        case SUCCESS(ACTION_TYPES.FETCH_QA_LIST):
            return {
                ...state,
                loading: false,
                entities: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.FETCH_QA):
            return {
                ...state,
                loading: false,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.CREATE_QA):
        case SUCCESS(ACTION_TYPES.UPDATE_QA):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.DELETE_QA):
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

const apiUrl = 'api/qas';

// Actions

export const getEntities: ICrudGetAllAction<IQA> = (page, size, sort) => ({
    type: ACTION_TYPES.FETCH_QA_LIST,
    payload: axios.get<IQA>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IQA> = id => {
    const requestUrl = `${apiUrl}/${id}`;
    return {
        type: ACTION_TYPES.FETCH_QA,
        payload: axios.get<IQA>(requestUrl)
    };
};

export const createEntity: ICrudPutAction<IQA> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.CREATE_QA,
        payload: axios.post(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const updateEntity: ICrudPutAction<IQA> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.UPDATE_QA,
        payload: axios.put(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const deleteEntity: ICrudDeleteAction<IQA> = id => async dispatch => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await dispatch({
        type: ACTION_TYPES.DELETE_QA,
        payload: axios.delete(requestUrl)
    });
    dispatch(getEntities());
    return result;
};

export const reset = () => ({
    type: ACTION_TYPES.RESET
});
