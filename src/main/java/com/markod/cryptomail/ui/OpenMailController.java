package com.markod.cryptomail.ui;

import com.markod.cryptomail.Main;
import com.markod.cryptomail.mail.MailingService;
import com.markod.cryptomail.mail.models.FileModel;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenMailController {

    private final Pattern PATTERN = Pattern.compile("(?<=<)(.*?)(?=>)");
    protected static OpenMailController INSTANCE;
    protected static Stage openMailStage;
    protected static Message message;
    protected static ArrayList<FileModel> attachmentModels = new ArrayList<>();

    @FXML
    protected Button attachmentsButton;

    @FXML
    protected String mailContent;

    @FXML
    protected WebView webView;

    @FXML
    protected void onDecryptButtonClick() {
        try {
            DecryptionController.decryptionStage = new Stage();
            Scene scene = new Scene(new FXMLLoader(Main.class.getResource(ScenesController.Scenes.DECRYPT.getResourceLocation())).load());
            DecryptionController.decryptionStage.setScene(scene);
            DecryptionController.decryptionStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onReplyButtonClick() {
        try {
            String address = getAddress(InternetAddress.toString(message.getFrom()));
            NewMailController.newMailStage = new Stage();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(ScenesController.Scenes.NEW_MAIL.getResourceLocation()));
            Scene scene = new Scene(loader.load());
            NewMailController.newMailStage.setScene(scene);
            loader.<NewMailController>getController().to.setDisable(true);
            loader.<NewMailController>getController().subject.setText("RE: " + message.getSubject());
            NewMailController.newMailStage.setTitle("Reply to: " + address);
            loader.<NewMailController>getController().to.setText(address);

            NewMailController.newMailStage.show();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onAttachmentsButtonClick() {
        try {
            ReceivedAttachmentsController.receivedAttachmentsStage = new Stage();
            Scene recievedAttachmentsStage = new Scene(new FXMLLoader(Main.class.getResource(ScenesController.Scenes.RECEIVED_ATTACHMENTS.getResourceLocation())).load());
            ReceivedAttachmentsController.receivedAttachmentsStage.setScene(recievedAttachmentsStage);
            ReceivedAttachmentsController.receivedAttachmentsStage.setTitle("Received Attachments");
            ReceivedAttachmentsController.receivedAttachmentsStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    protected void onCloseButtonClick() {
        openMailStage.close();
        INSTANCE = null;
    }

    @FXML
    protected void initialize() {
        try {
            mailContent = MailingService.getInstance().getMessageContent(message);
            attachmentModels = MailingService.getInstance().getFileModels(message);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        if (attachmentModels.isEmpty()) {
            attachmentsButton.setDisable(true);
        }

        if (mailContent != null) {
            webView.getEngine().loadContent(mailContent);
        }
    }

    protected static void fetch(Message msg) {
        message = msg;
    }

    private String getAddress(String address) {
        Matcher matcher = PATTERN.matcher(address);
        if (address.contains("<") || address.contains(">")) {
            if (matcher.find()) {
                return matcher.group();
            }
            else {
                throw new IllegalArgumentException();
            }
        }
        else {
            return address;
        }
    }
}
