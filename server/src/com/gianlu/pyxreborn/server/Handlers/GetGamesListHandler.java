package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GetGamesListHandler extends BaseHandler {
    public GetGamesListHandler() {
        super(Operations.GET_GAMES_LIST);
    }

    @Override
    public JsonObject handleRequest(Server server, JsonObject request, JsonObject response) throws GeneralException {
        JsonArray list = new JsonArray();
        for (Game game : server.games)
            list.add(game.toJson());

        response.add(Fields.GAMES_LIST.toString(), list);
        response.addProperty(Fields.MAX_GAMES.toString(), server.games.getMax());

        return response;
    }
}
