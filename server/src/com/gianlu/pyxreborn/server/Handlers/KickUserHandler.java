package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.KickReason;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class KickUserHandler extends BaseHandlerWithAdminUser {
    public KickUserHandler() {
        super(Operations.KICK);
    }

    @Override
    public JsonObject handleRequestForAdmin(Server server, @NotNull User user, JsonObject request, JsonObject response) throws GeneralException {
        JsonElement reason = request.get(Fields.KICK_REASON.toString());
        JsonElement nickname = request.get(Fields.NICKNAME.toString());
        if (nickname == null) throw new GeneralException(ErrorCodes.INVALID_REQUEST);
        User kickUser = server.users.findByNickname(nickname.getAsString());
        if (kickUser == null) throw new GeneralException(ErrorCodes.INVALID_NICKNAME);
        server.users.kickUser(kickUser, reason == null ? null : KickReason.parse(reason.getAsString()));
        return successful(response);
    }
}
