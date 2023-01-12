package com.markod.cryptomail.mail;

import com.markod.cryptomail.auth.IMAPAuthenticationService;
import com.markod.cryptomail.auth.SMTPAuthenticationService;
import com.markod.cryptomail.mail.models.FileModel;
import com.markod.cryptomail.util.IMail;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;

public class MailingService implements IMail<MailingService> {
    private static MailingService INSTANCE;
    private Mail mail;
    private Transport transport;

    private MailingService() {
        transport = SMTPAuthenticationService.getInstance().getConnector();
    }

    @Override
    public MailingService createMail(String subject, String text, InternetAddress recipient, FileModel... fileModels) {
        try {
            mail = new Mail(subject, text, recipient, fileModels);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    @Override
    public MailingService saveMail() {
        try {
            mail.save();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public void sendMail() {
        try {
            transport.sendMessage(mail.getMessage(), mail.getMessage().getAllRecipients());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message[] fetchInbox() {
        Message[] messages;


        try {
            Folder folder = getInbox();
            messages = folder.getMessages(folder.getMessageCount() - 50, folder.getMessageCount());
            setFetchProfiles(folder, messages);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    private void setFetchProfiles(Folder folder, Message[] messages) throws MessagingException {
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);
        fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
        folder.fetch(messages, fetchProfile);
    }

    private Folder getInbox() throws MessagingException {
        Folder folder = IMAPAuthenticationService.getInstance().getConnector().getFolder("Inbox");
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    @Override
    public MailingService getService() {
        return this;
    }

    @Override
    public void resetService() {
        mail = null;
        transport = null;
    }

    public static MailingService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MailingService();
        }

        return INSTANCE;
    }
}
