package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.Events;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Client extends PyxClientAdapter {
    private final Map<EventInterceptor, IEventListener> eventListener = new HashMap<>();

    public Client(URI serverUri, String nickname, @Nullable String sid) {
        super(serverUri, nickname, sid);
    }

    @Override
    public void onEvent(Events event, JsonObject request) {
        Logger.info(event.name() + " (" + event.toString() + "): " + request);

        for (Map.Entry<EventInterceptor, IEventListener> entry : eventListener.entrySet())
            if (entry.getKey().shouldIntercept(event, request))
                entry.getValue().onMessage(event, request);
    }

    public void addListener(@NotNull EventInterceptor interceptor, @NotNull IEventListener listener) {
        eventListener.put(interceptor, listener);
    }

    public interface EventInterceptor {
        boolean shouldIntercept(Events event, JsonObject request);
    }

    public interface IEventListener {
        void onMessage(Events event, JsonObject obj);
    }
}
