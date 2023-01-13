package com.markod.cryptomail.ui;

import com.markod.cryptomail.mail.models.FileModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class ReceivedAttachmentsController {
    protected static Stage receivedAttachmentsStage;

    @FXML
    protected ListView<String> attachmentsView;

    @FXML
    protected void onSaveSelectedButtonClick() {
        String home = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save attachment");
        fileChooser.setInitialFileName(attachmentsView.getSelectionModel().getSelectedItem());
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All files", "*.*"));
        File file = fileChooser.showSaveDialog(receivedAttachmentsStage);
        if (file != null) {
            new Thread(() -> writeToFile(file)).start();
        }
    }

    @FXML
    protected void onCloseButtonClick() {
        receivedAttachmentsStage.close();
    }

    @FXML
    protected void initialize() {
        for (FileModel f : OpenMailController.attachmentModels) {
            attachmentsView.getItems().add(f.fileName());
        }
    }

    private void writeToFile(File file) {
        if (file != null) {
            String fileName = attachmentsView.getSelectionModel().getSelectedItem();
            FileModel model = OpenMailController.attachmentModels.stream().filter(fileModel -> fileModel.fileName().equalsIgnoreCase(fileName)).findFirst().get();
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                InputStream stream = new ByteArrayInputStream(model.bytes());
                byte[] buffer = new byte[4096];
                int read;
                while ((read = stream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to save attachment!\n" + e.getMessage());
                alert.show();
                e.printStackTrace();
            }
        }
    }
}
