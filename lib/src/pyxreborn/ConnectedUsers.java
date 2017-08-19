package com.gianlu.pyxreborn;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Models.User;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class ConnectedUsers extends ArrayList<User> {
    private final PyxServerAdapter server;
    private final int maxUsers;

    public ConnectedUsers(PyxServerAdapter server, int maxUsers) {
        this.server = server;
        this.maxUsers = maxUsers;
    }

    public void checkAndAdd(String nickname) throws GeneralException {
        if (size() >= maxUsers)
            throw new GeneralException(ErrorCodes.TOO_MANY_USERS);
        else if (findByNickname(nickname) != null)
            throw new GeneralException(ErrorCodes.NICK_ALREADY_IN_USE);

        User user = new User(nickname, null);
        add(user);

        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.SESSION_ID.toString(), user.sessionId);
        server.sendMessage(user, obj);

        JsonObject obj1 = new JsonObject();
        obj1.addProperty(Fields.EVENT.toString(), Events.NEW_USER.toString());
        obj1.addProperty(Fields.NICKNAME.toString(), nickname);
        server.broadcastMessage(obj1);
    }

    @Nullable
    public User findByNickname(String nickname) {
        for (User user : this)
            if (Objects.equals(user.nickname, nickname))
                return user;

        return null;
    }

    @Nullable
    public User findBySessionId(String sid) {
        for (User user : this)
            if (Objects.equals(user.sessionId, sid))
                return user;

        return null;
    }
}
