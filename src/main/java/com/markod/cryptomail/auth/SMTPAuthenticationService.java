package com.markod.cryptomail.auth;

import com.markod.cryptomail.Main;
import com.markod.cryptomail.props.Provider;
import jakarta.mail.*;

import java.util.Properties;

public final class SMTPAuthenticationService extends AuthenticationService {

    private static SMTPAuthenticationService INSTANCE;
    private Transport transport;
    private Session session;
    private Properties properties;

    private SMTPAuthenticationService() { super(); }

    @Override
    protected void setProperties(String providerName) {
        properties = new Properties();
        Provider provider = Main.PROVIDERS.stream().filter(p -> p.getName().equals(providerName)).toList().get(0);

        properties.put("mail.smtp.auth", provider.isAuth());
        properties.put("mail.smtp.ssl.enable", true);
        properties.put("mail.smtp.ssl.protocols", provider.getProtocols());
        properties.put("mail.smtp.ssl.trust", provider.getTrust());
        properties.put("mail.smtp.starttls.enable", provider.isStarttls());
        properties.put("mail.smtp.host", provider.getSmtp());
        properties.put("mail.smtp.port", provider.getSmtpPort());
    }

    @Override
    protected void setSession() {
        session = Session.getInstance(properties, getAuthenticator());
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public int connect() {
        try {
            transport = session.getTransport("smtp");
            if (!transport.isConnected()) {
                transport.connect();
            }
            else {
                System.out.println("SMTP Transport already connected");
            }
            System.out.println("SMTP Transport connected.");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            return 2;
        } catch (MessagingException e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    @Override
    public int disconnect() {
        try {
            if (transport.isConnected()) {
                transport.close();
            }
            else {
                System.out.println("SMTP Transport not connected.");
                return 2;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public boolean isConnected() {
        return transport.isConnected();
    }

    @Override
    public Transport getConnector() {
        return transport;
    }

    @Override
    public AuthenticationService getService() {
        return this;
    }

    @Override
    public void resetService() {
        transport = null;
        session = null;
        properties = null;
    }

    public static SMTPAuthenticationService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SMTPAuthenticationService();
        }
        return INSTANCE;
    }
}
