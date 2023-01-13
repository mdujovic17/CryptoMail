package com.markod.cryptomail;

import com.markod.cryptomail.props.Provider;
import com.markod.cryptomail.ui.ScenesController;
import com.markod.cryptomail.util.PropertyReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;

public class Main extends Application {

    public static final HashSet<Provider> PROVIDERS;

    static {
        try {
            PROVIDERS = new PropertyReader(new File(Objects.requireNonNull(Main.class.getResource("providers.json")).toURI())).read();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Pair<String, String> credentials = null;
    public static void main(String[] args) throws URISyntaxException {
        if (args.length != 0) {
            credentials = new Pair<>(args[0], args[1]);
            args = null;
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ScenesController.setStage(primaryStage);
        ScenesController.setStageTitle("Login");
        ScenesController.setStageScene(new FXMLLoader(getClass().getResource(ScenesController.Scenes.LOGIN.getResourceLocation())));
    }
}