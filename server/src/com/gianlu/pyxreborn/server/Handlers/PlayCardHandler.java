package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Player;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.GameManager;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class PlayCardHandler extends BaseHandlerWithGameManager {
    public PlayCardHandler() {
        super(Operations.PLAY_CARD);
    }

    @Override
    public JsonObject handleRequest(Server server, @NotNull Player player, @NotNull GameManager manager, JsonObject request, JsonObject response) throws GeneralException {
        JsonElement cardId = request.get(Fields.CARD_ID.toString());
        if (cardId == null) throw new GeneralException(ErrorCodes.INVALID_REQUEST);
        manager.playCard(player, cardId.getAsInt());
        return successful(response);
    }
}
