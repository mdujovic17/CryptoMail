module CryptoMail {
    requires xstream;
    requires jettison;
    requires jakarta.mail;
    requires jakarta.activation;
    requires jasypt;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;

    opens com.markod.cryptomail.ui to javafx.fxml, java.base, javafx.web, org.controlsfx.controls;
    opens com.markod.cryptomail.mail.models to javafx.base;
    opens com.markod.cryptomail.props to xstream;

    exports com.markod.cryptomail;
}