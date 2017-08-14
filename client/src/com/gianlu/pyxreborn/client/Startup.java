package com.gianlu.pyxreborn.client;

import com.gianlu.consoleui.Answer;
import com.gianlu.consoleui.Choice.ChoiceAnswer;
import com.gianlu.consoleui.Choice.List.ListChoicePrompt;
import com.gianlu.consoleui.Confirmation.ConfirmationAnswer;
import com.gianlu.consoleui.Confirmation.ConfirmationPrompt;
import com.gianlu.consoleui.Confirmation.Value;
import com.gianlu.consoleui.ConsolePrompt;
import com.gianlu.consoleui.Input.InputAnswer;
import com.gianlu.consoleui.Input.InputPrompt;
import com.gianlu.consoleui.Input.InputValidator;
import com.gianlu.consoleui.InvalidInputException;
import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.google.gson.JsonObject;
import javafx.util.Pair;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Startup {
    private static final String LOGGING = "logging";
    private final ConsolePrompt prompt = new ConsolePrompt();
    private Client client;

    public static void main(String[] args) throws InterruptedException, IOException {
        AnsiConsole.systemInstall();

        Startup startup = new Startup();

        List<? extends Answer> result = startup.prompt.prompt(
                new ConfirmationPrompt.Builder()
                        .name(LOGGING)
                        .text("Do you want to enable logging?")
                        .defaultValue(Value.YES)
                        .build(),
                new InputPrompt.Builder()
                        .name(Fields.IP.toString())
                        .defaultValue("ws://127.0.0.1:6969")
                        .text("What's the server IP?")
                        .validator(new InputValidator() {
                            @Override
                            public void validate(@NotNull String s) throws InvalidInputException {
                                try {
                                    new URI(s);
                                } catch (URISyntaxException ex) {
                                    throw new InvalidInputException(ex.getMessage(), true);
                                }
                            }
                        })
                        .required(true)
                        .build(),
                new InputPrompt.Builder()
                        .name(Fields.NICKNAME.toString())
                        .text("Insert your nickname:")
                        .required(true)
                        .build(),
                new InputPrompt.Builder()
                        .name(Fields.SESSION_ID.toString())
                        .text("SID (empty if you're not reconnecting):")
                        .defaultValue("")
                        .required(false)
                        .build());

        Logger.setEnabled(((ConfirmationAnswer) result.get(0)).isConfirmed());

        String sid = ((InputAnswer) result.get(3)).getAnswer();
        startup.client = new Client(URI.create(((InputAnswer) result.get(1)).getAnswer()),
                ((InputAnswer) result.get(2)).getAnswer(),
                sid.isEmpty() ? null : sid);

        if (startup.client.connectBlocking()) {
            startup.mainMenu();
        } else {
            Logger.severe(new Exception("Failed connecting!"));
            System.exit(1);
        }
    }

    @Nullable
    private Pair<String, String> askForRequestParam() throws IOException {
        ConfirmationAnswer confirm = prompt.prompt(new ConfirmationPrompt.Builder()
                .text("Do you want to add additional params to the request?")
                .defaultValue(Value.NO)
                .name("additional")
                .build());

        if (confirm.isConfirmed()) {
            List<? extends Answer> pair = prompt.prompt(
                    new InputPrompt.Builder()
                            .text("Key:")
                            .name("key")
                            .required(true)
                            .build(),
                    new InputPrompt.Builder()
                            .text("Value:")
                            .name("value")
                            .required(true)
                            .build());

            return new Pair<>(((InputAnswer) pair.get(0)).getAnswer(), ((InputAnswer) pair.get(1)).getAnswer());
        } else {
            return null;
        }
    }

    private void mainMenu() throws IOException {
        ListChoicePrompt.Builder builder = new ListChoicePrompt.Builder();
        builder.text("Request operation:")
                .name("req");

        for (Operations op : Operations.values())
            builder.newItem().name(op.toString()).text(op.name()).add();

        ChoiceAnswer answer = prompt.prompt(builder.build());
        Operations op = Operations.parse(answer.getName());

        JsonObject req = client.createRequest(op);

        Pair<String, String> additionalParams;
        do {
            additionalParams = askForRequestParam();
            if (additionalParams != null) req.addProperty(additionalParams.getKey(), additionalParams.getValue());
        } while (additionalParams != null);

        System.out.println("REQUEST: " + req);

        JsonObject resp;
        try {
            resp = client.sendMessageBlocking(req);
        } catch (InterruptedException | PyxException ex) {
            Logger.severe(ex);
            mainMenu();
            return;
        }

        System.out.println("RESPONSE: " + resp);

        mainMenu();
    }
}
