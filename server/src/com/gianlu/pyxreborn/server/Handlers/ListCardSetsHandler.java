package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.CardSet;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ListCardSetsHandler extends BaseHandler {
    public ListCardSetsHandler() {
        super(Operations.LIST_CARD_SETS);
    }

    @Override
    public JsonObject handleRequest(Server server, JsonObject request, JsonObject response) throws GeneralException {
        JsonArray array = new JsonArray();
        for (CardSet set : server.cardSets) array.add(set.toCompactJson());
        response.add(Fields.CARD_SET.toString(), array);
        return response;
    }
}
