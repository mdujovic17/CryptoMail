package com.markod.cryptomail.ui;

import com.markod.cryptomail.Main;
import com.markod.cryptomail.auth.AuthenticationService;
import com.markod.cryptomail.auth.IMAPAuthenticationService;
import com.markod.cryptomail.auth.SMTPAuthenticationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController {
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9_!*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    @FXML
    private Button loginButton;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    @FXML
    protected void onLoginButtonClick() {
        Matcher matcher = PATTERN.matcher(username.getText());

        if (matcher.find() && password.getText().length() >= 8) {
            Object lock = new Object();
            AtomicBoolean threadReady = new AtomicBoolean(false);
            AtomicInteger log = new AtomicInteger();
            new Thread(() -> {
                SMTPAuthenticationService.getInstance().login(username.getText(), password.getText());
                IMAPAuthenticationService.getInstance().login(username.getText(), password.getText());
                log.set(SMTPAuthenticationService.getInstance().connect());
                log.set(IMAPAuthenticationService.getInstance().connect());

                synchronized (lock) {
                    threadReady.set(true);
                    lock.notifyAll();
                }

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
                                ScenesController.setStageScene(new FXMLLoader(Main.class.getResource(ScenesController.Scenes.INBOX.getResourceLocation())));
                                ScenesController.setStageTitle("Mail Application");
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

            new Thread(() -> {
                if (!loginButton.isDisabled()) {
                    username.setDisable(true);
                    password.setDisable(true);
                    loginButton.setDisable(true);
                }

                synchronized (lock) {
                    while (!threadReady.get()) {
                        try {
                            lock.wait(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                username.setDisable(false);
                password.setDisable(false);
                loginButton.setDisable(false);
            }).start();
        }
    }

    @FXML
    protected void initialize() {
        if (Main.credentials != null) {
            this.username.setText(Main.credentials.getKey());
            this.password.setText(Main.credentials.getValue());
            Main.credentials = null;
            onLoginButtonClick();
        }
    }
}
