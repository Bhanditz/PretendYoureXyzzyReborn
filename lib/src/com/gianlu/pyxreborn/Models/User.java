package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Utils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public class User implements Jsonable {
    public final String nickname;
    public final InetSocketAddress address;
    public final String sessionId;
    public final boolean isAdmin;
    public long disconnectedAt = -1;

    public User(String nickname, @Nullable String sessionId, InetSocketAddress address, boolean isAdmin) {
        this.nickname = nickname;
        this.address = address;
        this.isAdmin = isAdmin;
        if (sessionId == null) this.sessionId = Utils.generateAlphanumericString(16);
        else this.sessionId = sessionId;
    }

    public boolean isDisconnected() {
        return disconnectedAt != -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return nickname.equals(user.nickname) && sessionId.equals(user.sessionId);
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.NICKNAME.toString(), nickname);
        return obj;
    }
}
