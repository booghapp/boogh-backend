import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITelegramChat, defaultValue } from 'app/shared/model/telegram-chat.model';

export const ACTION_TYPES = {
    FETCH_TELEGRAMCHAT_LIST: 'telegramChat/FETCH_TELEGRAMCHAT_LIST',
    FETCH_TELEGRAMCHAT: 'telegramChat/FETCH_TELEGRAMCHAT',
    CREATE_TELEGRAMCHAT: 'telegramChat/CREATE_TELEGRAMCHAT',
    UPDATE_TELEGRAMCHAT: 'telegramChat/UPDATE_TELEGRAMCHAT',
    DELETE_TELEGRAMCHAT: 'telegramChat/DELETE_TELEGRAMCHAT',
    RESET: 'telegramChat/RESET'
};

const initialState = {
    loading: false,
    errorMessage: null,
    entities: [] as ReadonlyArray<ITelegramChat>,
    entity: defaultValue,
    updating: false,
    updateSuccess: false
};

export type TelegramChatState = Readonly<typeof initialState>;

// Reducer

export default (state: TelegramChatState = initialState, action): TelegramChatState => {
    switch (action.type) {
        case REQUEST(ACTION_TYPES.FETCH_TELEGRAMCHAT_LIST):
        case REQUEST(ACTION_TYPES.FETCH_TELEGRAMCHAT):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                loading: true
            };
        case REQUEST(ACTION_TYPES.CREATE_TELEGRAMCHAT):
        case REQUEST(ACTION_TYPES.UPDATE_TELEGRAMCHAT):
        case REQUEST(ACTION_TYPES.DELETE_TELEGRAMCHAT):
            return {
                ...state,
                errorMessage: null,
                updateSuccess: false,
                updating: true
            };
        case FAILURE(ACTION_TYPES.FETCH_TELEGRAMCHAT_LIST):
        case FAILURE(ACTION_TYPES.FETCH_TELEGRAMCHAT):
        case FAILURE(ACTION_TYPES.CREATE_TELEGRAMCHAT):
        case FAILURE(ACTION_TYPES.UPDATE_TELEGRAMCHAT):
        case FAILURE(ACTION_TYPES.DELETE_TELEGRAMCHAT):
            return {
                ...state,
                loading: false,
                updating: false,
                updateSuccess: false,
                errorMessage: action.payload
            };
        case SUCCESS(ACTION_TYPES.FETCH_TELEGRAMCHAT_LIST):
            return {
                ...state,
                loading: false,
                entities: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.FETCH_TELEGRAMCHAT):
            return {
                ...state,
                loading: false,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.CREATE_TELEGRAMCHAT):
        case SUCCESS(ACTION_TYPES.UPDATE_TELEGRAMCHAT):
            return {
                ...state,
                updating: false,
                updateSuccess: true,
                entity: action.payload.data
            };
        case SUCCESS(ACTION_TYPES.DELETE_TELEGRAMCHAT):
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

const apiUrl = 'api/telegram-chats';

// Actions

export const getEntities: ICrudGetAllAction<ITelegramChat> = (page, size, sort) => ({
    type: ACTION_TYPES.FETCH_TELEGRAMCHAT_LIST,
    payload: axios.get<ITelegramChat>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<ITelegramChat> = id => {
    const requestUrl = `${apiUrl}/${id}`;
    return {
        type: ACTION_TYPES.FETCH_TELEGRAMCHAT,
        payload: axios.get<ITelegramChat>(requestUrl)
    };
};

export const createEntity: ICrudPutAction<ITelegramChat> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.CREATE_TELEGRAMCHAT,
        payload: axios.post(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const updateEntity: ICrudPutAction<ITelegramChat> = entity => async dispatch => {
    const result = await dispatch({
        type: ACTION_TYPES.UPDATE_TELEGRAMCHAT,
        payload: axios.put(apiUrl, cleanEntity(entity))
    });
    dispatch(getEntities());
    return result;
};

export const deleteEntity: ICrudDeleteAction<ITelegramChat> = id => async dispatch => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await dispatch({
        type: ACTION_TYPES.DELETE_TELEGRAMCHAT,
        payload: axios.delete(requestUrl)
    });
    dispatch(getEntities());
    return result;
};

export const reset = () => ({
    type: ACTION_TYPES.RESET
});
