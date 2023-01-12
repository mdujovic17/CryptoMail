package com.markod.cryptomail.ui;

import javafx.beans.binding.ListBinding;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashSet;

public class AttachmentsController {
    protected static HashSet<File> attachments = new HashSet<>();
    protected static Stage attachmentStage;

    @FXML
    protected ListView<String> list;

    @FXML
    protected void onAddAttachmentsButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose attachments");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All files", "*.*"));
        attachments.addAll(fileChooser.showOpenMultipleDialog(attachmentStage));
        for (File f : attachments) {
            list.getItems().add(f.getName());
        }
    }

    @FXML
    protected void onCloseButtonClick() {
        if (!list.getItems().isEmpty()) {
            list.getItems().clear();
            list.refresh();
        }
        attachmentStage.close();
    }

    @FXML
    protected void onClearAllButtonClick() {
        attachments.clear();
        if (!list.getItems().isEmpty()) {
            list.getItems().clear();
            list.refresh();
        }
    }

    @FXML
    protected void onRemoveSelectedButtonClick() {
        String selected = list.getSelectionModel().getSelectedItem();
        attachments.removeIf(f -> f.getName().equals(selected));
        if (!list.getItems().isEmpty()) {
            list.getItems().remove(selected);
            list.refresh();
        }
    }

    @FXML
    protected void initialize() {
        if (!attachments.isEmpty()) {
            for (File f : attachments) {
                list.getItems().add(f.getName());
            }
        }
    }
}
