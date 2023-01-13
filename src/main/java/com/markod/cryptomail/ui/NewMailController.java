package com.markod.cryptomail.ui;

import com.markod.cryptomail.Main;
import com.markod.cryptomail.mail.Mail;
import com.markod.cryptomail.mail.MailingService;
import com.markod.cryptomail.mail.models.FileModel;
import com.markod.cryptomail.util.Encryption;
import com.markod.cryptomail.util.Triple;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class NewMailController {
    private final ArrayList<Triple<Encryption.PBEAlgorithm, Encryption.DigestAlgorithm, String>> algorithmList = new ArrayList<>();
    protected static Stage newMailStage;

    @FXML
    protected TextField to;
    @FXML
    protected TextField subject;
    @FXML
    protected ToggleSwitch encryptionSwitch;
    @FXML
    protected ChoiceBox<String> encryption;
    @FXML
    protected PasswordField password;
    @FXML
    protected TextArea content;

    @FXML
    protected void onSendButtonClick() {
        String subject = this.subject.getText();
        String content = this.content.getText();
        //File[] attachments = AttachmentsController.attachments.toArray(new File[0]);
        ArrayList<FileModel> models = new ArrayList<>();

        for (File f : AttachmentsController.attachments) {
            try (FileInputStream fileInputStream = new FileInputStream(f)) {
                models.add(new FileModel(f.getName(), fileInputStream.readAllBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (to.getText() != null && !to.getText().equals("")) {
            new Thread(() -> {
                try {
                    if (encryptionSwitch.isSelected()) {
                        int selection = encryption.getSelectionModel().getSelectedIndex();
                        try {
                            MailingService.getInstance().createMail(subject, content, new InternetAddress(to.getText()), models.toArray(new FileModel[0])).encrypt(password.getText(), algorithmList.get(selection).algorithm(), algorithmList.get(selection).digest());
                        } catch (AddressException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        MailingService.getInstance().createMail(subject, content, new InternetAddress(to.getText()), models.toArray(new FileModel[0]));
                    }

                    MailingService.getInstance().saveMail().sendMail();
                } catch (AddressException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        onCancelButtonClick();
    }

    @FXML
    protected void onAttachmentButtonClick() {
        try {
            AttachmentsController.attachmentStage = new Stage();
            Scene scene = new Scene(FXMLLoader.load(Main.class.getResource(ScenesController.Scenes.ATTACHMENTS.getResourceLocation())));
            AttachmentsController.attachmentStage.setScene(scene);
            AttachmentsController.attachmentStage.setTitle("Attachments");
            AttachmentsController.attachmentStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onCancelButtonClick() {
        if (!to.isDisabled()) {
            try {
                ScenesController.setStageScene(new FXMLLoader(Main.class.getResource(ScenesController.Scenes.INBOX.getResourceLocation())));
                ScenesController.setStageTitle("Mail application");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            newMailStage.close();
        }
    }

    @FXML
    protected void initialize() {
        algorithmList.add(new Triple<>(Encryption.PBEAlgorithm.MD5_TRIPLE_DES, Encryption.DigestAlgorithm.MD5, "MD5 with triple DES"));
        algorithmList.add(new Triple<>(Encryption.PBEAlgorithm.SHA1_AES256, Encryption.DigestAlgorithm.SHA1, "SHA-1 with AES256"));
        algorithmList.add(new Triple<>(Encryption.PBEAlgorithm.SHA256_AES256, Encryption.DigestAlgorithm.SHA256, "SHA-256 with AES256"));
        algorithmList.add(new Triple<>(Encryption.PBEAlgorithm.SHA512_AES256, Encryption.DigestAlgorithm.SHA512, "SHA-512 with AES256"));

        for (Triple<Encryption.PBEAlgorithm, Encryption.DigestAlgorithm, String> triple : algorithmList) {
            encryption.getItems().add(triple.name());
        }

        encryption.getSelectionModel().select(0);

        encryptionSwitch.selectedProperty().addListener((event) -> {
            if (encryptionSwitch.isSelected()) {
                password.setDisable(false);
                encryption.setDisable(false);
            }
            else {
                password.setDisable(true);
                encryption.setDisable(true);
            }
        });
    }
}
