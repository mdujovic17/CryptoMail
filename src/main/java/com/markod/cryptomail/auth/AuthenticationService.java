package com.markod.cryptomail.auth;

import com.markod.cryptomail.util.IAuth;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Service;
import jakarta.mail.Session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract sealed class AuthenticationService implements IAuth<AuthenticationService, Service> permits IMAPAuthenticationService, SMTPAuthenticationService {
    private boolean authenticated = false;
    protected String email;
    private Authenticator authenticator;

    public void login(String username, String password) {
        this.email = email;
        setAuthenticator(username, password);
        String provider = getProvider(username);
        setProperties(provider);
        setSession();
    }

    protected abstract void setProperties(String provider);

    protected abstract void setSession();

    public abstract Session getSession();

    private void setAuthenticator(String username, String password) {
        authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }

    protected Authenticator getAuthenticator() {
        return authenticator;
    }

    protected String getProvider(String email) {
        Pattern pattern = Pattern.compile("(?<=@)(.*?)(?=\\.)");
        Matcher matcher = pattern.matcher(email);

        if (matcher.find()) {
            switch (matcher.group()) {
                case "gmail" -> {
                    setAuthenticated(true);
                    return "google";
                }
                case "yahoo" -> {
                    setAuthenticated(true);
                    return "yahoo";
                }
                case "outlook" -> {
                    setAuthenticated(true);
                    return "outlook";
                }
                default -> {
                    System.err.println("No matching provider");
                    System.exit(1);
                }
            }
        }

        return null;
    }

    private void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getEmail() {
        return email;
    }
}
