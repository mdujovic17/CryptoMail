package com.markod.cryptomail.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScenesController {
    public enum Scenes {
        LOGIN("ui/login.fxml"),
        NEW_MAIL("ui/new_mail.fxml"),
        ATTACHMENTS("ui/attachments.fxml"),
        RECEIVED_ATTACHMENTS("ui/received_attachments.fxml"),
        INBOX("ui/inbox.fxml"),
        OPEN_MAIL("ui/open_mail.fxml"),
        DECRYPT("ui/decryption.fxml");
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
