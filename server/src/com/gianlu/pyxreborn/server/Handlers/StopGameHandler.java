package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Models.Player;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.GameManager;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class StopGameHandler extends BaseHandlerWithGameManager {
    public StopGameHandler() {
        super(Operations.STOP_GAME);
    }

    @Override
    public JsonObject handleRequest(Server server, @NotNull Player player, @NotNull GameManager manager, JsonObject request, JsonObject response) throws GeneralException {
        manager.stop();
        return successful(response);
    }
}
