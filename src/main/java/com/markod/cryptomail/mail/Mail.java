package com.markod.cryptomail.mail;

import com.markod.cryptomail.auth.SMTPAuthenticationService;
import com.markod.cryptomail.mail.models.FileModel;
import com.markod.cryptomail.util.Encryption;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import java.util.ArrayList;
import java.util.List;

public class Mail {
    private final MimeMessage message;
    private final MimeMultipart messageBody;
    private final ArrayList<FileModel> fileModels;
    private String text;

    public Mail(String subject, String text, InternetAddress recipient, FileModel... fileModels) throws MessagingException {
        this.message = new MimeMessage(SMTPAuthenticationService.getInstance().getSession());
        this.messageBody = new MimeMultipart();
        this.fileModels = new ArrayList<>();

        this.message.setSubject(subject);
        this.message.setRecipients(Message.RecipientType.TO, String.valueOf(recipient));
        this.text = text;
        this.fileModels.addAll(List.of(fileModels));
    }

    private void attachDataSource(String fileName, byte[] bytes) throws MessagingException {
        MimeBodyPart data = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(bytes, "text/plain;charset=UTF-8");

        data.setDataHandler(new DataHandler(source));
        data.setFileName(fileName);

        messageBody.addBodyPart(data);
    }

    private void attachFiles() throws MessagingException {
        for (FileModel fileModel : fileModels) {
            attachDataSource(fileModel.fileName(), fileModel.bytes());
        }
    }

    private void encryptText(String password, Encryption.PBEAlgorithm pbeAlgorithm, Encryption.DigestAlgorithm digestAlgorithm) {
        this.text = Encryption.encryptText(this.text, Encryption.getHashFromPassword(password, digestAlgorithm), pbeAlgorithm);
    }

    private void encryptFiles(String password, Encryption.PBEAlgorithm pbeAlgorithm, Encryption.DigestAlgorithm digestAlgorithm) {
        fileModels.replaceAll(fileModel -> new FileModel(fileModel.fileName(), Encryption.encryptBytes(fileModel.bytes(), Encryption.getHashFromPassword(password, digestAlgorithm), pbeAlgorithm)));
    }

    public Mail encrypt(String password, Encryption.PBEAlgorithm pbeAlgorithm, Encryption.DigestAlgorithm digestAlgorithm) {
        encryptFiles(password, pbeAlgorithm, digestAlgorithm);
        encryptText(password, pbeAlgorithm, digestAlgorithm);

        return this;
    }

    public void save() throws MessagingException {
        MimeBodyPart textBody = new MimeBodyPart();
        textBody.setText(this.text);
        messageBody.addBodyPart(textBody);
        attachFiles();
        message.setContent(messageBody);
        message.saveChanges();
    }

    public MimeMessage getMessage() {
        return message;
    }
}
