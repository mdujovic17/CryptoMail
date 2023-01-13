package com.markod.cryptomail.ui;

import com.markod.cryptomail.mail.MailingService;
import com.markod.cryptomail.mail.models.FileModel;
import com.markod.cryptomail.util.Encryption;
import com.markod.cryptomail.util.Triple;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import java.io.IOException;
import java.util.ArrayList;

public class DecryptionController {
    protected static Stage decryptionStage;
    private final ArrayList<Triple<Encryption.PBEAlgorithm, Encryption.DigestAlgorithm, String>> algorithmList = new ArrayList<>();

    @FXML
    protected ChoiceBox<String> encryption;
    @FXML
    protected PasswordField password;

    @FXML
    protected void onDecryptionButtonClick() {
        int selected = encryption.getSelectionModel().getSelectedIndex();

        try {
            OpenMailController.INSTANCE.webView.getEngine().loadContent(Encryption.decryptText(MailingService.getInstance().getMessageContent(OpenMailController.message), Encryption.getHashFromPassword(password.getText(), algorithmList.get(selected).digest()), algorithmList.get(selected).algorithm()));
            OpenMailController.attachmentModels.replaceAll(fileModel -> new FileModel(fileModel.fileName(), Encryption.decryptBytes(fileModel.bytes(), Encryption.getHashFromPassword(password.getText(), algorithmList.get(selected).digest()), algorithmList.get(selected).algorithm())));
        } catch (EncryptionOperationNotPossibleException e) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Unable to Decrypt email\n" + e.getMessage());
            a.show();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        decryptionStage.close();
    }

    @FXML
    protected void onCancelButtonClick() {
        decryptionStage.close();
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
    }
}
