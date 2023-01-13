package com.markod.cryptomail.mail;

import com.markod.cryptomail.auth.IMAPAuthenticationService;
import com.markod.cryptomail.auth.SMTPAuthenticationService;
import com.markod.cryptomail.mail.models.FileModel;
import com.markod.cryptomail.util.Encryption;
import com.markod.cryptomail.util.IMail;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

    public MailingService encrypt(String password, Encryption.PBEAlgorithm pbeAlgorithm, Encryption.DigestAlgorithm digestAlgorithm) {
        mail.encrypt(password, pbeAlgorithm, digestAlgorithm);
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

    public String getMessageContent(Message message) throws MessagingException, IOException {
        String mailContent = "";
        Object content = message.getContent();

        if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                if (multipart.getBodyPart(i).getFileName() == null) {
                    if (multipart.getBodyPart(i).isMimeType("text/plain") || multipart.getBodyPart(i).isMimeType("text/html")) {
                        mailContent = (String) multipart.getBodyPart(i).getContent();
                    }
                }
            }
        }
        else if (content instanceof String contentText) {
            mailContent = contentText;
        }

        return mailContent;
    }

    public ArrayList<FileModel> getFileModels(Message message) throws MessagingException, IOException {
        ArrayList<Pair<String, byte[]>> pairs = getMessageAttachments(message);
        ArrayList<FileModel> fileModels = new ArrayList<>();

        for (Pair<String, byte[]> pair : pairs) {
            fileModels.add(new FileModel(pair.getKey(), pair.getValue()));
        }

        return fileModels;
    }

    private ArrayList<Pair<String, byte[]>> getMessageAttachments(Message message) throws MessagingException, IOException {
        ArrayList<Pair<String, byte[]>> pairs = new ArrayList<>();
        Object content = message.getContent();

        if (content instanceof  Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                if (multipart.getBodyPart(i).getContent() instanceof InputStream || multipart.getBodyPart(i).getContent() instanceof String) {
                    if (Part.ATTACHMENT.equalsIgnoreCase(multipart.getBodyPart(i).getDisposition()) || multipart.getBodyPart(i).getFileName() != null) {
                        DataHandler handler = multipart.getBodyPart(i).getDataHandler();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        handler.getInputStream().transferTo(byteArrayOutputStream);

                        byte[] bytes = byteArrayOutputStream.toByteArray();

                        pairs.add(new Pair<>(multipart.getBodyPart(i).getFileName(), bytes));
                    }
                }
            }
        }
        return pairs;
    }
}
