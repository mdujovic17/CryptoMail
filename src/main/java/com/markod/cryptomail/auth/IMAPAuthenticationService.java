package com.markod.cryptomail.auth;

import com.markod.cryptomail.Main;
import com.markod.cryptomail.props.Provider;
import jakarta.mail.*;

import java.util.Properties;

public final class IMAPAuthenticationService extends AuthenticationService {

    private static IMAPAuthenticationService INSTANCE;
    private Store store;
    private Session session;
    private Properties properties;

    private IMAPAuthenticationService() {
        super();
    }

    @Override
    protected void setProperties(String providerName) {
        properties = new Properties();
        Provider provider = Main.PROVIDERS.stream().filter(p -> p.getName().equals(providerName)).toList().get(0);
        properties.put("mail.imap.auth", provider.isAuth());
        properties.put("mail.imap.ssl.enable", true);
        properties.put("mail.imap.ssl.protocols", provider.getProtocols());
        properties.put("mail.imap.ssl.trust", provider.getTrust());
        properties.put("mail.imap.starttls.enable", provider.isStarttls());
        properties.put("mail.imap.host", provider.getImap());
        properties.put("mail.imap.port", provider.getImapPort());
    }

    @Override
    protected void setSession() {
        this.session = Session.getInstance(properties, getAuthenticator());
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public int connect() {
        try {
            store = session.getStore("imap");
            if (!store.isConnected()) {
                store.connect();
            }
            else {
                System.out.println("IMAP Store already connected");
            }
            System.out.println("IMAP Store connected.");
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
            if (store.isConnected()) {
                store.close();
            }
            else {
                System.out.println("IMAP Store not connected");
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
        return store.isConnected();
    }

    @Override
    public Store getConnector() {
        return store;
    }

    @Override
    public AuthenticationService getService() {
        return this;
    }

    @Override
    public void resetService() {
        store = null;
        session = null;
        properties = null;
    }

    public static IMAPAuthenticationService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IMAPAuthenticationService();
        }
        return INSTANCE;
    }
}