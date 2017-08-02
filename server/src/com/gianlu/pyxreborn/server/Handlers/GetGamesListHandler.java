package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GetGamesListHandler extends BaseHandler {
    public GetGamesListHandler() {
        super(Operations.GET_GAMES_LIST);
    }

    @Override
    public JsonObject handleRequest(Server server, JsonObject response) {
        JsonArray list = new JsonArray();
        Gson gson = new Gson();
        for (Game game : server.games)
            list.add(gson.toJsonTree(game));

        response.add(Fields.GAMES_LIST.toString(), list);
        response.addProperty(Fields.MAX_GAMES.toString(), server.games.getMax());

        return response;
    }
}
