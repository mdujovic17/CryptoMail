package com.markod.cryptomail.util;

import com.markod.cryptomail.mail.models.FileModel;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;

public interface IMail<T> extends IService<T> {
    T createMail(String subject, String text, InternetAddress recipient, FileModel... fileModels);
    T saveMail();
    void sendMail();
    Message[] fetchInbox();
}
