import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IHonk, defaultValue } from 'app/shared/model/honk.model';

export const ACTION_TYPES = {
    FETCH_HONK_LIST: 'honk/FETCH_HONK_LIST',
    FETCH_HONK: 'honk/FETCH_HONK',
    CREATE_HONK: 'honk/CREATE_HONK',
    UPDATE_HONK: 'honk/UPDATE_HONK',
    DELETE_HONK: 'honk/DELETE_HONK',
    RESET: 'honk/RESET'
};

const initialState = {
    loading: false,
    errorMessage: null,
    entities: [] as ReadonlyArray<IHonk>,
    entity: defaultValue,
    updating: false,
    totalItems: 0,
    updateSuccess: false
};

export type HonkState = Readonly<typeof initialState>;

// Reducer

export default (state: HonkState = initialState, action): HonkState => {
    switch (action.type) {
        case REQUEST(ACTION_TYPES.FETCH_HONK_LIST):
        case REQUEST(ACTION_TYPES.FETCH_HONK):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                loading: true
            };
        case REQUEST(ACTION_TYPES.CREATE_HONK):
        case REQUEST(ACTION_TYPES.UPDATE_HONK):
        case REQUEST(ACTION_TYPES.DELETE_HONK):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                updating: true
            };
        case FAILURE(ACTION_TYPES.FETCH_HONK_LIST):
        case FAILURE(ACTION_TYPES.FETCH_HONK):
        case FAILURE(ACTION_TYPES.CREATE_HONK):
        case FAILURE(ACTION_TYPES.UPDATE_HONK):
        case FAILURE(ACTION_TYPES.DELETE_HONK):
            return {
                ...state,
                loading: false,
                updating: false,
                updateSuccess: false,
                errorMessage: action.payload
            };
        case SUCCESS(ACTION_TYPES.FETCH_HONK_LIST):
            return {
                ...state,
                loading: false,
                totalItems: action.payload.headers['x-total-count'],
                entities: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.FETCH_HONK):
            return {
                ...state,
                loading: false,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.CREATE_HONK):
        case SUCCESS(ACTION_TYPES.UPDATE_HONK):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.DELETE_HONK):
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

const apiUrl = 'api/honks';

// Actions

export const getEntities: ICrudGetAllAction<IHonk> = (page, size, sort) => {
    const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
    return {
        type: ACTION_TYPES.FETCH_HONK_LIST,
        payload: axios.get<IHonk>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
    };
};

export const getEntity: ICrudGetAction<IHonk> = id => {
    const requestUrl = `${apiUrl}/${id}`;
    return {
        type: ACTION_TYPES.FETCH_HONK,
        payload: axios.get<IHonk>(requestUrl)
    };
};

export const createEntity: ICrudPutAction<IHonk> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.CREATE_HONK,
        payload: axios.post(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const updateEntity: ICrudPutAction<IHonk> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.UPDATE_HONK,
        payload: axios.put(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const deleteEntity: ICrudDeleteAction<IHonk> = id => async dispatch => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await dispatch({
        type: ACTION_TYPES.DELETE_HONK,
        payload: axios.delete(requestUrl)
    });
    dispatch(getEntities());
    return result;
};

export const reset = () => ({
    type: ACTION_TYPES.RESET
});
