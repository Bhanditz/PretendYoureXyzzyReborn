package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import de.codeshelf.consoleui.elements.ConfirmChoice;
import de.codeshelf.consoleui.prompt.*;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

// FIXME: All these is pretty much broken :)
public class Startup {
    private static final String LOGGING = "logging";
    private static final String NEXT_ACTION = "nextAction";
    private final ConsolePrompt prompt = new ConsolePrompt();

    public static void main(String[] args) throws InterruptedException, IOException {
        AnsiConsole.systemInstall();

        Startup startup = new Startup();

        PromptBuilder promptBuilder = startup.prompt.getPromptBuilder();
        promptBuilder.createConfirmPromp()
                .name(LOGGING)
                .defaultValue(ConfirmChoice.ConfirmationValue.YES)
                .message("Do you want to enable logging?")
                .addPrompt();

        promptBuilder.createInputPrompt()
                .name(Fields.IP.toString())
                .defaultValue("ws://127.0.0.1:6969")
                .message("Insert the server IP: ")
                .addPrompt();

        promptBuilder.createInputPrompt()
                .name(Fields.NICKNAME.toString())
                .message("Insert a nickname: ")
                .addPrompt();

        Map<String, ? extends PromtResultItemIF> result = startup.prompt.prompt(promptBuilder.build());

        Logger.setEnabled(((ConfirmResult) result.get(LOGGING)).getConfirmed() == ConfirmChoice.ConfirmationValue.YES);
        Client client = new Client(URI.create(((InputResult) result.get(Fields.IP.toString())).getInput()),
                ((InputResult) result.get(Fields.NICKNAME.toString())).getInput());

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

        PromptBuilder promptBuilder = prompt.getPromptBuilder();
        promptBuilder.createListPrompt()
                .name(NEXT_ACTION)
                .message("What you wanna do next?")
                .newItem().text("List games").name(Operations.GET_GAMES_LIST.toString()).add()
                .newItem().text("List users").name(Operations.GET_USERS_LIST.toString()).add()
                .newItem().text("Create game").name(Operations.CREATE_GAME.toString()).add()
                .addPrompt();

        Map<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
        Operations next = Operations.parse(((ListResult) result.get(NEXT_ACTION)).getSelectedId());
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
