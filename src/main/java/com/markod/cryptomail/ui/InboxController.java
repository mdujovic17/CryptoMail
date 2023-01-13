package com.markod.cryptomail.ui;

import com.markod.cryptomail.Main;
import com.markod.cryptomail.auth.IMAPAuthenticationService;
import com.markod.cryptomail.auth.SMTPAuthenticationService;
import com.markod.cryptomail.mail.MailingService;
import com.markod.cryptomail.mail.models.MessageModel;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.Match;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InboxController {
    private final Pattern PATTERN = Pattern.compile("(?<=<)(.*?)(?=>)");
    protected Message[] messages;

    @FXML
    protected Button openButton;
    @FXML
    protected TableView<MessageModel> inbox;
    @FXML
    protected TableColumn<MessageModel, String> date;
    @FXML
    protected TableColumn<MessageModel, String> from;
    @FXML
    protected TableColumn<MessageModel, String> subject;
    @FXML
    protected TableColumn<MessageModel, Integer> id;

    @FXML
    protected void onNewMailButtonClick() {
        try {
            ScenesController.setStageScene(new FXMLLoader(Main.class.getResource(ScenesController.Scenes.NEW_MAIL.getResourceLocation())));
            ScenesController.setStageTitle("Send new mail");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onRefreshButtonClick() {
        initialize();
    }

    @FXML
    protected void onOpenButtonClick() {
        new Thread(() -> {
            MessageModel model = inbox.getSelectionModel().getSelectedItem();
            Message message = Arrays.stream(messages).filter(m -> m.getMessageNumber() == model.messageId()).toList().get(0);

            OpenMailController.fetch(message);
            Platform.runLater(() -> {
                OpenMailController.openMailStage = new Stage();
                FXMLLoader loader = new FXMLLoader(Main.class.getResource(ScenesController.Scenes.OPEN_MAIL.getResourceLocation()));
                try {
                    Scene openMailScene  = new Scene(loader.load());
                    OpenMailController.openMailStage.setScene(openMailScene);
                    OpenMailController.INSTANCE = loader.getController();
                    OpenMailController.openMailStage.setTitle(String.format("%s <%s>", message.getSubject(), InternetAddress.toString(message.getFrom())));
                } catch (MessagingException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to open mail!\n" + e.getMessage());
                    alert.show();
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                OpenMailController.openMailStage.show();
            });
        }).start();
    }

    @FXML
    protected void onLogoutButtonClick() {
        new Thread(() -> {
            AtomicInteger log = new AtomicInteger();
            log.set(SMTPAuthenticationService.getInstance().disconnect());
            log.set(IMAPAuthenticationService.getInstance().disconnect());

            Platform.runLater(() -> {
                Alert alert = null;
                switch (log.get()) {
                    case 1 -> {
                        alert = new Alert(Alert.AlertType.ERROR, "Unable to connect to service, check username and password");
                    }
                    case 2 -> {
                        alert = new Alert(Alert.AlertType.ERROR, "Unable to determine the provider");
                    }
                    case 0 -> {
                        try {
                            ScenesController.setStageScene(new FXMLLoader(Main.class.getResource(ScenesController.Scenes.LOGIN.getResourceLocation())));
                            ScenesController.setStageTitle("Login");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    default -> {
                        throw new IllegalArgumentException("Unknown return value");
                    }
                }
                if (alert != null) {
                    alert.show();
                }
            });

        }).start();
    }

    @FXML
    protected void initialize() {
        subject.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().messageSubject()));
        date.setCellValueFactory(p -> new SimpleStringProperty(Date.from(p.getValue().messageDate().toInstant()).toString()));
        from.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().messageSender()));
        id.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().messageId()).asObject());

        messages = MailingService.getInstance().fetchInbox();

        setTableItems();
        setTableSortOrder();
        setDoubleClickEvent();
        focusTableRow();
    }

    private void focusTableRow() {
        inbox.getSelectionModel().select(0);
        inbox.getSelectionModel().focus(0);
    }

    private void setDoubleClickEvent() {
        inbox.setRowFactory(view -> {
            TableRow<MessageModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    onOpenButtonClick();
                }
            });
            return row;
        });
    }

    private void setTableSortOrder() {
        id.setComparator(id.getComparator().reversed());
        inbox.getSortOrder().add(id);
    }

    private void setTableItems() {
        ObservableList<MessageModel> models = FXCollections.observableArrayList();

        for (Message m : messages) {
            try {
                if (InternetAddress.toString(m.getFrom()).contains("<") && InternetAddress.toString(m.getFrom()).contains(">")) {
                    Matcher matcher = PATTERN.matcher(InternetAddress.toString(m.getFrom()));
                    if (matcher.find()) {
                        models.add(new MessageModel(m.getMessageNumber(), m.getSubject(), matcher.group(), m.getReceivedDate()));
                    }
                }
                else {
                    models.add(new MessageModel(m.getMessageNumber(), m.getSubject(), InternetAddress.toString(m.getFrom()), m.getReceivedDate()));
                }
            } catch (MessagingException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to read mail data!\n" + e.getMessage());
                alert.show();
                e.printStackTrace();
            }
        }

        inbox.setItems(models);
    }
}
