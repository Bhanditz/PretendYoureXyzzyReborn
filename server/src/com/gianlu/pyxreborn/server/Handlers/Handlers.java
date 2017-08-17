package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Operations;

import java.util.HashMap;
import java.util.Map;

public class Handlers {
    public final static Map<Operations, Class<? extends BaseHandler>> LIST = new HashMap<>();

    static {
        LIST.put(Operations.LIST_GAMES, ListGamesHandler.class);
        LIST.put(Operations.LIST_USERS, ListUsersHandler.class);
        LIST.put(Operations.CREATE_GAME, CreateGameHandler.class);
        LIST.put(Operations.JOIN_GAME, JoinGameHandler.class);
        LIST.put(Operations.START_GAME, StartGameHandler.class);
        LIST.put(Operations.PLAY_CARD, PlayCardHandler.class);
        LIST.put(Operations.JUDGE, JudgeHandler.class);
        LIST.put(Operations.CHANGE_GAME_OPTIONS, ChangeGameOptionsHandler.class);
        LIST.put(Operations.LIST_CARDS, ListCardsHandler.class);
        LIST.put(Operations.LIST_CARD_SETS, ListCardSetsHandler.class);
        LIST.put(Operations.CHAT, ChatHandler.class);
        LIST.put(Operations.GAME_CHAT, GameChatHandler.class);
        LIST.put(Operations.GET_GAME, GetGameHandler.class);
        LIST.put(Operations.KICK, KickUserHandler.class);
    }
}
