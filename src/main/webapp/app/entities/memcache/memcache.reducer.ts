import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IMemcache, defaultValue } from 'app/shared/model/memcache.model';

export const ACTION_TYPES = {
    FETCH_MEMCACHE_LIST: 'memcache/FETCH_MEMCACHE_LIST',
    FETCH_MEMCACHE: 'memcache/FETCH_MEMCACHE',
    CREATE_MEMCACHE: 'memcache/CREATE_MEMCACHE',
    UPDATE_MEMCACHE: 'memcache/UPDATE_MEMCACHE',
    DELETE_MEMCACHE: 'memcache/DELETE_MEMCACHE',
    RESET: 'memcache/RESET'
};

const initialState = {
    loading: false,
    errorMessage: null,
    entities: [] as ReadonlyArray<IMemcache>,
    entity: defaultValue,
    updating: false,
    updateSuccess: false
};

export type MemcacheState = Readonly<typeof initialState>;

// Reducer

export default (state: MemcacheState = initialState, action): MemcacheState => {
    switch (action.type) {
        case REQUEST(ACTION_TYPES.FETCH_MEMCACHE_LIST):
        case REQUEST(ACTION_TYPES.FETCH_MEMCACHE):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                loading: true
            };
        case REQUEST(ACTION_TYPES.CREATE_MEMCACHE):
        case REQUEST(ACTION_TYPES.UPDATE_MEMCACHE):
        case REQUEST(ACTION_TYPES.DELETE_MEMCACHE):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                updating: true
            };
        case FAILURE(ACTION_TYPES.FETCH_MEMCACHE_LIST):
        case FAILURE(ACTION_TYPES.FETCH_MEMCACHE):
        case FAILURE(ACTION_TYPES.CREATE_MEMCACHE):
        case FAILURE(ACTION_TYPES.UPDATE_MEMCACHE):
        case FAILURE(ACTION_TYPES.DELETE_MEMCACHE):
            return {
                ...state,
                loading: false,
                updating: false,
                updateSuccess: false,
                errorMessage: action.payload
            };
        case SUCCESS(ACTION_TYPES.FETCH_MEMCACHE_LIST):
            return {
                ...state,
                loading: false,
                entities: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.FETCH_MEMCACHE):
            return {
                ...state,
                loading: false,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.CREATE_MEMCACHE):
        case SUCCESS(ACTION_TYPES.UPDATE_MEMCACHE):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.DELETE_MEMCACHE):
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

const apiUrl = 'api/memcaches';

// Actions

export const getEntities: ICrudGetAllAction<IMemcache> = (page, size, sort) => ({
    type: ACTION_TYPES.FETCH_MEMCACHE_LIST,
    payload: axios.get<IMemcache>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IMemcache> = id => {
    const requestUrl = `${apiUrl}/${id}`;
    return {
        type: ACTION_TYPES.FETCH_MEMCACHE,
        payload: axios.get<IMemcache>(requestUrl)
    };
};

export const createEntity: ICrudPutAction<IMemcache> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.CREATE_MEMCACHE,
        payload: axios.post(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const updateEntity: ICrudPutAction<IMemcache> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.UPDATE_MEMCACHE,
        payload: axios.put(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const deleteEntity: ICrudDeleteAction<IMemcache> = id => async dispatch => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await dispatch({
        type: ACTION_TYPES.DELETE_MEMCACHE,
        payload: axios.delete(requestUrl)
    });
    dispatch(getEntities());
    return result;
};

export const reset = () => ({
    type: ACTION_TYPES.RESET
});
