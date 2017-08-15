package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ListUsersHandler extends BaseHandler {
    public ListUsersHandler() {
        super(Operations.LIST_USERS);
    }

    @Override
    public JsonObject handleRequest(Server server, JsonObject request, JsonObject response) throws GeneralException {
        JsonArray list = new JsonArray();
        for (User user : server.users)
            list.add(user.toJson());

        response.add(Fields.USERS_LIST.toString(), list);
        response.addProperty(Fields.MAX_USERS.toString(), server.users.getMax());

        return response;
    }
}
