package com.gianlu.pyxreborn.client.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class StartupUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static <T> void loadScene(@Nullable Stage stage, String title, String layout, @NotNull T controller) {
        FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(layout));
        loader.setController(controller);

        try {
            Parent root = loader.load();
            if (stage == null) stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        loadScene(stage, "Register - Pretend You're Xyzzy Reborn", "Register.fxml", new Register(stage));
    }
}
