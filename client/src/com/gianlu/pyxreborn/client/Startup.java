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
import org.fusesource.jansi.Ansi;
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
        Client client = new Client(URI.create(((InputAnswer) result.get(1)).getAnswer()),
                ((InputAnswer) result.get(2)).getAnswer());

        if (client.connectBlocking()) {
            startup.mainMenu();
        } else {
            Logger.severe(new Exception("Failed connecting!"));
        }
    }

    private void clear() {
        Ansi.ansi().eraseScreen();
    }

    private void mainMenu() throws IOException {
        clear();

        ChoiceAnswer result = prompt.prompt(new ChoicePrompt.Builder()
                .name(NEXT_ACTION)
                .text("What you wanna do next?")
                .newItem().text("List games").key('g').name(Operations.GET_GAMES_LIST.toString()).add()
                .newItem().text("List users").key('u').name(Operations.GET_USERS_LIST.toString()).add()
                .newItem().text("Create game").key('c').name(Operations.CREATE_GAME.toString()).add()
                .build());

        Operations next = Operations.parse(result.getName());
        System.out.println("NEXT: " + next);

        /*
        client.sendMessage(client.createRequest(Operations.GET_GAMES_LIST), new PyxClientAdapter.IMessage() {
            @Override
            public void onMessage(JsonObject resp) {
                JsonArray games = resp.getAsJsonArray(Fields.GAMES_LIST.toString());
                if (games.size() == 0) {
                    System.out.println("There are no games!");
                    return;
                }

                PromptBuilder builder = prompt.getPromptBuilder();
                ListPromptBuilder gamesPrompt = builder.createListPrompt()
                        .name(Fields.GID.toString())
                        .message("Select a game to join:");

                for (JsonElement game : games)
                    gamesPrompt.newItem()
                            .text(game.getAsJsonObject()
                                    .getAsJsonObject(Fields.HOST.toString())
                                    .get(Fields.NICKNAME.toString()).getAsString())
                            .name(game.getAsJsonObject()
                                    .get(Fields.GID.toString()).getAsString())
                            .add();

                gamesPrompt.addPrompt();

                try {
                    Map<String, ? extends PromtResultItemIF> result = prompt.prompt(builder.build());
                    System.out.println("GAME SELECTED: " + ((ListResult) result.get(Fields.GID.toString())).getSelectedId());
                } catch (IOException ex) {
                    onException(ex);
                }
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        });
        */
    }
}
