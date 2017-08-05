package com.gianlu.pyxreborn.client;

import com.gianlu.consoleui.Answer;
import com.gianlu.consoleui.Choice.ChoiceAnswer;
import com.gianlu.consoleui.Choice.ChoicePrompt;
import com.gianlu.consoleui.Confirmation.ConfirmationAnswer;
import com.gianlu.consoleui.Confirmation.ConfirmationPrompt;
import com.gianlu.consoleui.Confirmation.Value;
import com.gianlu.consoleui.ConsolePrompt;
import com.gianlu.consoleui.Input.InputAnswer;
import com.gianlu.consoleui.Input.InputPrompt;
import com.gianlu.consoleui.Input.InputValidator;
import com.gianlu.consoleui.InvalidInputException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Startup {
    private static final String LOGGING = "logging";
    private static final String NEXT_ACTION = "nextAction";
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
                        .build());

        Logger.setEnabled(((ConfirmationAnswer) result.get(0)).isConfirmed());

        startup.client = new Client(URI.create(((InputAnswer) result.get(1)).getAnswer()),
                ((InputAnswer) result.get(2)).getAnswer());

        if (startup.client.connectBlocking()) {
            startup.mainMenu();
        } else {
            Logger.severe(new Exception("Failed connecting!"));
        }
    }

    private void createGame() {
        throw new UnsupportedOperationException("Not implemented yet!"); // TODO
    }

    private void mainMenu() {
        ChoiceAnswer result;
        try {
            result = prompt.prompt(new ChoicePrompt.Builder()
                    .name(NEXT_ACTION)
                    .text("What you wanna do next?")
                    .newItem().text("List games").key('g').name(Operations.GET_GAMES_LIST.toString()).add()
                    .newItem().text("List users").key('u').name(Operations.GET_USERS_LIST.toString()).add()
                    .newItem().text("Create game").key('c').name(Operations.CREATE_GAME.toString()).add()
                    .build());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Operations next = Operations.parse(result.getName());
        if (next == null) throw new IllegalArgumentException("next can't be null!");

        switch (next) {
            case GET_GAMES_LIST:
                client.sendMessage(client.createRequest(Operations.GET_GAMES_LIST), new PyxClientAdapter.IMessage() {
                    @Override
                    public void onMessage(JsonObject resp) {
                        JsonArray games = resp.getAsJsonArray(Fields.GAMES_LIST.toString());
                        if (games.size() == 0) {
                            try {
                                ConfirmationAnswer answer = prompt.prompt(new ConfirmationPrompt.Builder()
                                        .text("There are no games! Do you want to create one?")
                                        .defaultValue(Value.YES)
                                        .name("createGame")
                                        .build());

                                if (answer.isConfirmed()) createGame();
                                else mainMenu();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            return;
                        }

                        ChoicePrompt.Builder builder = new ChoicePrompt.Builder();
                        builder.name(Fields.GID.toString())
                                .text("Select a game to join:");

                        // TODO: I must use ListChoicePrompt but it's not finished, this doesn't work.
                        for (JsonElement game : games)
                            builder.newItem()
                                    .text(game.getAsJsonObject()
                                            .getAsJsonObject(Fields.HOST.toString())
                                            .get(Fields.NICKNAME.toString()).getAsString())
                                    .name(game.getAsJsonObject()
                                            .get(Fields.GID.toString()).getAsString())
                                    .add();

                        try {
                            ChoiceAnswer answer = prompt.prompt(builder.build());
                            System.out.println(answer);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onException(Exception ex) {
                        ex.printStackTrace();
                    }
                });
                break;
            case GET_USERS_LIST:
                client.sendMessage(client.createRequest(Operations.GET_USERS_LIST), new PyxClientAdapter.IMessage() {
                    @Override
                    public void onMessage(JsonObject resp) {
                        JsonArray users = resp.getAsJsonArray(Fields.USERS_LIST.toString());

                        StringBuilder builder = new StringBuilder();
                        boolean first = true;
                        for (JsonElement element : users) {
                            if (!first) builder.append(", ");
                            first = false;
                            builder.append(element.getAsJsonObject().get(Fields.NICKNAME.toString()).getAsString());
                        }

                        System.out.println("Online users: " + builder.toString());
                        mainMenu();
                    }

                    @Override
                    public void onException(Exception ex) {
                        ex.printStackTrace();
                    }
                });
                break;
            case CREATE_GAME:
                createGame();
                break;
        }
    }
}
