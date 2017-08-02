package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Random;

public class User {
    public final String nickname;
    public final InetSocketAddress address;
    public final String sessionId;

    public User(String nickname, @Nullable String sessionId, InetSocketAddress address) {
        this.nickname = nickname;
        this.address = address;
        if (sessionId == null) this.sessionId = generateNewSessionId();
        else this.sessionId = sessionId;
    }

    @NotNull
    private String generateNewSessionId() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(16);
        for (int i = 0; i <= 15; i++) {
            if (random.nextBoolean()) builder.append(String.valueOf(random.nextInt(10)));
            else builder.append(Utils.ALPHABET.charAt(random.nextInt(Utils.ALPHABET.length())));
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return nickname.equals(user.nickname) && sessionId.equals(user.sessionId);
    }

}
