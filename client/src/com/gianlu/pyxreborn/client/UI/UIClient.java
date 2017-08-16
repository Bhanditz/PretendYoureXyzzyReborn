package com.gianlu.pyxreborn.client.UI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class UIClient {
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
}
