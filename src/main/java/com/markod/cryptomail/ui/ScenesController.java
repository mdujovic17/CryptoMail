package com.markod.cryptomail.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScenesController {
    public enum Scenes {
        LOGIN("login.fxml"),
        NEW_MAIL("new_mail.fxml"),
        ATTACHMENTS("attachments.fxml"),
        RECEIVED_ATTACHMENTS("received_attachments.fxml"),
        INBOX("inbox.fxml"),
        OPEN_MAIL("open_mail.fxml"),
        DECRYPT("decrypt.fxml");
        private final String resourceLocation;

        Scenes(String resourceLocation) {
            this.resourceLocation = resourceLocation;
        }

        public String getResourceLocation() {
            return resourceLocation;
        }
    }

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void setStageTitle(String title) {
        stage.setTitle(title);
    }

    public static void setStageScene(FXMLLoader loader) throws IOException {
        if (stage.isShowing()) {
            stage.hide();
        }

        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
