package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.client.UI.RegisterUI;
import com.gianlu.pyxreborn.client.UI.UIClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Startup extends Application {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0 && args[0].contains("--console")) new ConsoleClient();
        else launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        UIClient.loadScene(stage, "Register - Pretend You're Xyzzy Reborn", "Register.fxml", new RegisterUI(stage));
    }
}
