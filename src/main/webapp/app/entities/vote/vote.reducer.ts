import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IVote, defaultValue } from 'app/shared/model/vote.model';

export const ACTION_TYPES = {
    FETCH_VOTE_LIST: 'vote/FETCH_VOTE_LIST',
    FETCH_VOTE: 'vote/FETCH_VOTE',
    CREATE_VOTE: 'vote/CREATE_VOTE',
    UPDATE_VOTE: 'vote/UPDATE_VOTE',
    DELETE_VOTE: 'vote/DELETE_VOTE',
    RESET: 'vote/RESET'
};

const initialState = {
    loading: false,
    errorMessage: null,
    entities: [] as ReadonlyArray<IVote>,
    entity: defaultValue,
    updating: false,
    totalItems: 0,
    updateSuccess: false
};

export type VoteState = Readonly<typeof initialState>;

// Reducer

export default (state: VoteState = initialState, action): VoteState => {
    switch (action.type) {
        case REQUEST(ACTION_TYPES.FETCH_VOTE_LIST):
        case REQUEST(ACTION_TYPES.FETCH_VOTE):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                loading: true
            };
        case REQUEST(ACTION_TYPES.CREATE_VOTE):
        case REQUEST(ACTION_TYPES.UPDATE_VOTE):
        case REQUEST(ACTION_TYPES.DELETE_VOTE):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                updating: true
            };
        case FAILURE(ACTION_TYPES.FETCH_VOTE_LIST):
        case FAILURE(ACTION_TYPES.FETCH_VOTE):
        case FAILURE(ACTION_TYPES.CREATE_VOTE):
        case FAILURE(ACTION_TYPES.UPDATE_VOTE):
        case FAILURE(ACTION_TYPES.DELETE_VOTE):
            return {
                ...state,
                loading: false,
                updating: false,
                updateSuccess: false,
                errorMessage: action.payload
            };
        case SUCCESS(ACTION_TYPES.FETCH_VOTE_LIST):
            return {
                ...state,
                loading: false,
                totalItems: action.payload.headers['x-total-count'],
                entities: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.FETCH_VOTE):
            return {
                ...state,
                loading: false,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.CREATE_VOTE):
        case SUCCESS(ACTION_TYPES.UPDATE_VOTE):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.DELETE_VOTE):
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

const apiUrl = 'api/votes';

// Actions

export const getEntities: ICrudGetAllAction<IVote> = (page, size, sort) => {
    const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
    return {
        type: ACTION_TYPES.FETCH_VOTE_LIST,
        payload: axios.get<IVote>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
    };
};

export const getEntity: ICrudGetAction<IVote> = id => {
    const requestUrl = `${apiUrl}/${id}`;
    return {
        type: ACTION_TYPES.FETCH_VOTE,
        payload: axios.get<IVote>(requestUrl)
    };
};

export const createEntity: ICrudPutAction<IVote> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.CREATE_VOTE,
        payload: axios.post(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const updateEntity: ICrudPutAction<IVote> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.UPDATE_VOTE,
        payload: axios.put(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const deleteEntity: ICrudDeleteAction<IVote> = id => async dispatch => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await dispatch({
        type: ACTION_TYPES.DELETE_VOTE,
        payload: axios.delete(requestUrl)
    });
    dispatch(getEntities());
    return result;
};

export const reset = () => ({
    type: ACTION_TYPES.RESET
});
