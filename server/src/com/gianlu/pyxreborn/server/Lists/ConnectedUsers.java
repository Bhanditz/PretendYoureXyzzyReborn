package com.gianlu.pyxreborn.server.Lists;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.server.PyxServerAdapter;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectedUsers extends ArrayList<User> {
    private static final int GENERAL_TASKS_PERIOD = 10; // sec
    private final PyxServerAdapter server;
    private final int maxUsers;
    private final int reconnectDelay;
    private final GeneralTasks tasks;

    public ConnectedUsers(PyxServerAdapter server) {
        this.server = server;
        this.maxUsers = server.config.maxUsers;
        this.reconnectDelay = server.config.reconnectDelay * 1000;
        this.tasks = new GeneralTasks();
        new Timer().scheduleAtFixedRate(tasks, 0, GENERAL_TASKS_PERIOD * 1000);
    }

    public User checkAndAdd(String nickname, @Nullable String sid, InetSocketAddress address) throws GeneralException {
        if (size() >= maxUsers) throw new GeneralException(ErrorCodes.TOO_MANY_USERS);
        User alreadyConnectedUser = findByNickname(nickname);
        if (alreadyConnectedUser != null) {
            if (alreadyConnectedUser.isDisconnected()) {
                if (Objects.equals(alreadyConnectedUser.sessionId, sid)) {
                    alreadyConnectedUser.disconnectedAt = -1;

                    JsonObject obj = new JsonObject();
                    obj.addProperty(Fields.SESSION_ID.toString(), alreadyConnectedUser.sessionId);
                    server.sendMessage(alreadyConnectedUser, obj);

                    return alreadyConnectedUser;
                } else {
                    throw new GeneralException(ErrorCodes.INVALID_SID);
                }
            } else {
                throw new GeneralException(ErrorCodes.NICK_ALREADY_IN_USE);
            }
        }

        User user = new User(nickname, null, address);

        JsonObject obj = Utils.event(Events.NEW_USER);
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

    public void removeUser(User user, boolean remote) {
        if (remote) {
            user.disconnectedAt = System.currentTimeMillis(); // We're giving a chance to reconnect to the user
        } else {
            Game game = server.games.playingIn(user);
            if (game != null) server.games.leaveGame(game, user);

            remove(user);

            server.broadcastMessage(Utils.event(Events.USER_LEFT));
        }
    }

    public void removeUser(InetSocketAddress address, boolean remote) {
        User user = findByAddress(address);
        if (user != null) removeUser(user, remote);
    }

    public int getMax() {
        return maxUsers;
    }

    private class GeneralTasks extends TimerTask {

        @Override
        public void run() {
            // Remove user if it's been disconnected for too long
            for (int i = size() - 1; i >= 0; i--) {
                User user = get(i);
                if (user.isDisconnected() && System.currentTimeMillis() - user.disconnectedAt >= reconnectDelay)
                    removeUser(user, false);
            }
        }
    }
}
