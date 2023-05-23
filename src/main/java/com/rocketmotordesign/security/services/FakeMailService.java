package com.rocketmotordesign.security.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
@Profile("dev")
public class FakeMailService implements IMailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FakeMailService.class);
    private final String mailSenderAddress;

    public FakeMailService(@Value("${mail.sender}") String mailSenderAddress) {
        this.mailSenderAddress = mailSenderAddress;
    }

    @Override
    public void sendMessage(String sujet, String texte, String destinataires) throws MessagingException {
        logMail(sujet, texte, destinataires);
    }

    @Override
    public void sendHtmlMessage(String sujet, String texte, String destinataires) throws MessagingException {
        logMail(sujet, texte, destinataires);
    }

    private void logMail(String sujet, String texte, String destinataires) {
        StringBuilder stringBuilder = new StringBuilder("\n================= MAIL SEND =================")
                .append("\n\tFROM    : " + mailSenderAddress)
                .append("\n\tTO      : " + destinataires)
                .append("\n\tSUBJECT : " + sujet + "\n")
                .append("------------ MESSAGE ---------\n")
                .append(texte)
                .append("\n=========================================================\n");
        LOGGER.warn("FAKE MAIL SEND : {}", stringBuilder.toString());
    }
}
