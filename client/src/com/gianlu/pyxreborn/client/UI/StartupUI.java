package com.gianlu.pyxreborn.client.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class StartupUI extends Application {
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @NotNull
    public static Stage getStage() {
        if (stage == null) throw new IllegalStateException("Application not yet started!");
        return stage;
    }

    public static <T> T loadScene(Stage stage, String title, String layout) {
        FXMLLoader loader = new FXMLLoader(StartupUI.class.getResource(layout));

        try {
            Parent root = loader.load();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            return loader.getController();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        StartupUI.stage = stage;
        loadScene(stage, "Register", "Register.fxml");
        stage.show();
    }
}
