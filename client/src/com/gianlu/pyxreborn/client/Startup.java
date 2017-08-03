package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.codeshelf.consoleui.elements.ConfirmChoice;
import de.codeshelf.consoleui.prompt.*;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Startup {
    private static final String NO_LOGGING = "noLogging";

    public static void main(String[] args) throws InterruptedException, IOException {
        AnsiConsole.systemInstall();

        ConsolePrompt prompt = new ConsolePrompt();

        PromptBuilder promptBuilder = prompt.getPromptBuilder();
        promptBuilder.createConfirmPromp()
                .name(NO_LOGGING)
                .defaultValue(ConfirmChoice.ConfirmationValue.YES)
                .message("Do you want to turn off logging?")
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

        Map<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());

        Logger.getGlobal().setLevel(((ConfirmResult) result.get(NO_LOGGING)).getConfirmed() == ConfirmChoice.ConfirmationValue.YES ? Level.OFF : Level.ALL);

        Client client = new Client(URI.create(((InputResult) result.get(Fields.IP.toString())).getInput()),
                ((InputResult) result.get(Fields.NICKNAME.toString())).getInput());

        if (client.connectBlocking()) {
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
        } else {
            System.out.println("FAILED CONNECTING!");
        }
    }
}
