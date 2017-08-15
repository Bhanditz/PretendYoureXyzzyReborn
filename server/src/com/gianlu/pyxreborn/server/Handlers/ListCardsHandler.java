package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.CardSet;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ListCardsHandler extends BaseHandler {
    public ListCardsHandler() {
        super(Operations.LIST_CARDS);
    }

    @Override
    public JsonObject handleRequest(Server server, JsonObject request, JsonObject response) throws GeneralException {
        JsonElement cardSetId = request.get(Fields.CARD_SET_ID.toString());
        if (cardSetId == null) throw new GeneralException(ErrorCodes.INVALID_REQUEST);
        CardSet set = server.cardSets.findCardSetById(cardSetId.getAsInt());
        if (set == null) throw new GeneralException(ErrorCodes.INVALID_CARD_SET_ID);
        response.add(Fields.CARD_SET.toString(), set.toJson());
        return response;
    }
}
