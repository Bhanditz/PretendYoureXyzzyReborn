package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class ChatHandler extends BaseHandlerWithUser {
    public ChatHandler() {
        super(Operations.CHAT);
    }

    @Override
    public JsonObject handleRequest(Server server, @NotNull User user, JsonObject request, JsonObject response) throws GeneralException {
        JsonElement text = request.get(Fields.TEXT.toString());
        if (text == null) throw new GeneralException(ErrorCodes.INVALID_REQUEST);
        JsonObject obj = Utils.event(Events.CHAT);
        obj.addProperty(Fields.NICKNAME.toString(), user.nickname);
        obj.add(Fields.TEXT.toString(), text);
        server.broadcastMessage(obj);
        return successful(response);
    }
}
