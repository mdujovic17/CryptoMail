package com.markod.cryptomail.props;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Provider {
    private String name, smtp, imap, protocols;
    private char trust;
    private int imapPort, smtpPort;
    private boolean auth, starttls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmtp() {
        return smtp;
    }

    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }

    public String getImap() {
        return imap;
    }

    public void setImap(String imap) {
        this.imap = imap;
    }

    public String getProtocols() {
        return protocols;
    }

    public void setProtocols(String protocols) {
        this.protocols = protocols;
    }

    public char getTrust() {
        return trust;
    }

    public void setTrust(char trust) {
        this.trust = trust;
    }

    public int getImapPort() {
        return imapPort;
    }

    public void setImapPort(int imapPort) {
        this.imapPort = imapPort;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isStarttls() {
        return starttls;
    }

    public void setStarttls(boolean starttls) {
        this.starttls = starttls;
    }
}
