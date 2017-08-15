package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Operations;

import java.util.HashMap;
import java.util.Map;

public class Handlers {
    public final static Map<Operations, Class<? extends BaseHandler>> LIST = new HashMap<>();

    static {
        LIST.put(Operations.GET_GAMES_LIST, GetGamesListHandler.class);
        LIST.put(Operations.GET_USERS_LIST, GetUsersListHandler.class);
        LIST.put(Operations.CREATE_GAME, CreateGameHandler.class);
        LIST.put(Operations.JOIN_GAME, JoinGameHandler.class);
        LIST.put(Operations.START_GAME, StartGameHandler.class);
        LIST.put(Operations.PLAY_CARD, PlayCardHandler.class);
        LIST.put(Operations.JUDGE, JudgeHandler.class);
        LIST.put(Operations.CHANGE_GAME_OPTIONS, ChangeGameOptionsHandler.class);
    }
}
