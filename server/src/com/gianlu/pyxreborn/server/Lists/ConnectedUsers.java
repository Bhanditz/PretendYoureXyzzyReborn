package com.gianlu.pyxreborn.server.Lists;

import com.gianlu.pyxreborn.Annotations.AdminOnly;
import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.KickReason;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.Player;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.server.PyxServerAdapter;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.*;

public class ConnectedUsers extends ArrayList<User> {
    private static final int GENERAL_TASKS_PERIOD = 10; // sec
    private final PyxServerAdapter server;
    private final int maxUsers;
    private final int reconnectDelay;

    public ConnectedUsers(PyxServerAdapter server) {
        this.server = server;
        this.maxUsers = server.config.maxUsers;
        this.reconnectDelay = server.config.reconnectDelay * 1000;
        new Timer().scheduleAtFixedRate(new GeneralTasks(), 0, GENERAL_TASKS_PERIOD * 1000);
    }

    /**
     * Kicks an user from the server. Not allowing reconnection.
     */
    @AdminOnly
    public void kickUser(@NotNull User user, @Nullable KickReason reason) {
        Game playingIn = server.games.playingIn(user);
        if (playingIn != null) {
            Player player = playingIn.findPlayerByNickname(user.nickname);
            if (player != null) server.games.kickPlayer(playingIn, player, reason);
        }

        WebSocket socket = server.findWebSocketByAddress(user.address);
        if (socket == null) return;
        socket.close(CloseFrame.POLICY_VALIDATION, (reason == null ? KickReason.GENERAL_KICK : reason).toString());
    }

    /**
     * Adds an user to the server.
     *
     * @param admin if true the user must connect at every cost, even by kicking other players
     */
    public User checkAndAdd(String nickname, @Nullable String sid, InetSocketAddress address, boolean admin) throws GeneralException {
        if (size() >= maxUsers) {
            if (admin) kickUser(get(new Random().nextInt(size())), KickReason.ADMIN_LOGGED);
            else throw new GeneralException(ErrorCodes.TOO_MANY_USERS);
        }

        User alreadyConnectedUser = findByNickname(nickname);
        if (admin && alreadyConnectedUser != null) kickUser(alreadyConnectedUser, KickReason.ADMIN_LOGGED);
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

        if (nickname.isEmpty() || nickname.length() < 5 || (!admin && nickname.equals("xyzzy")))
            throw new GeneralException(ErrorCodes.INVALID_NICKNAME);

        User user = new User(nickname, null, address, admin);

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

    /**
     * Remove the specified {@link User}.
     *
     * @param remote if true the user won't be disconnected immediately.
     *               Its session can be restored by providing the session ID. See {@link PyxServerAdapter#onWebsocketHandshakeReceivedAsServer(WebSocket, Draft, ClientHandshake)}.
     *               The user will be permanently deleted after the reconnect delay. See {@link ConnectedUsers.GeneralTasks}.
     */
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

    /**
     * Removes the user associated with the specified {@link InetSocketAddress}.
     */
    public void removeUser(InetSocketAddress address, boolean remote) {
        User user = findByAddress(address);
        if (user != null) removeUser(user, remote);
    }

    public int getMax() {
        return maxUsers;
    }

    /**
     * General tasks:
     * <p>
     * - Remove an user if it's been disconnected for too long
     */
    private class GeneralTasks extends TimerTask {

        @Override
        public void run() {
            for (int i = size() - 1; i >= 0; i--) {
                User user = get(i);
                if (user.isDisconnected() && System.currentTimeMillis() - user.disconnectedAt >= reconnectDelay)
                    removeUser(user, false);
            }
        }
    }
}
