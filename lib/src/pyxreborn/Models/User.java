package com.gianlu.pyxreborn.Models;

import com.beust.jcommander.internal.Nullable;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class User {
    public final String nickname;
    public final String sessionId;

    public User(String nickname, @Nullable String sessionId) {
        this.nickname = nickname;
        if (sessionId == null) this.sessionId = generateNewSessionId();
        else this.sessionId = sessionId;
    }

    @NotNull
    private String generateNewSessionId() {
        return String.valueOf(new Random().nextInt()); // TODO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return nickname.equals(user.nickname) && sessionId.equals(user.sessionId);
    }

}
