package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.User;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Objects;

public class ConnectedUsers extends ArrayList<User> {
    private final PyxServerAdapter server;
    private final int maxUsers;

    public ConnectedUsers(PyxServerAdapter server, int maxUsers) {
        this.server = server;
        this.maxUsers = maxUsers;
    }

    public User checkAndAdd(String nickname, InetSocketAddress address) throws GeneralException {
        if (size() >= maxUsers) throw new GeneralException(ErrorCodes.TOO_MANY_USERS);
        if (findByNickname(nickname) != null) throw new GeneralException(ErrorCodes.NICK_ALREADY_IN_USE);

        User user = new User(nickname, null, address);

        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.EVENT.toString(), Events.NEW_USER.toString());
        obj.addProperty(Fields.NICKNAME.toString(), nickname);
        server.broadcastMessage(obj); // This way we don't send the broadcast to the user itself

        add(user);

        JsonObject obj1 = new JsonObject();
        obj1.addProperty(Fields.SESSION_ID.toString(), user.sessionId);
        server.sendMessage(user, obj1);

        return user;
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

    @Nullable
    public User findByAddress(InetSocketAddress address) {
        for (User user : this)
            if (Objects.equals(user.address, address))
                return user;

        return null;
    }

    public void removeUser(InetSocketAddress address, boolean remote) { // TODO: Should save state for like 60 sec to allow reconnect if disconnection is remote
        User user = findByAddress(address);
        if (user != null) {
            Game game = server.games.playingIn(user);
            if (game != null) server.games.leaveGame(game, user);

            remove(user);

            JsonObject obj = new JsonObject();
            obj.addProperty(Fields.EVENT.toString(), Events.USER_LEFT.toString());
            server.broadcastMessage(obj);
        }
    }

    public int getMax() {
        return maxUsers;
    }
}
