package com.markod.cryptomail.mail.models;


import java.util.Date;

public record MessageModel(int messageId, String messageSubject, String messageSender, Date messageDate) {}
