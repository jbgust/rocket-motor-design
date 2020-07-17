package com.rocketmotordesign.security.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {


    private final String mailSenderAddress;
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender, @Value("${mail.sender}") String mailSenderAddress) {
        this.mailSender = mailSender;
        this.mailSenderAddress = mailSenderAddress;
    }

    /**
     * Envoi de mail
     * @param sujet
     * @param texte
     * @param destinataires (email séparé par ',')
     */
    public void sendMessage(String sujet, String texte, String destinataires) throws MessagingException {
        sendMessage(sujet, texte, destinataires, false);

    }

    /**
     * Envoi de mail
     * @param sujet
     * @param texte
     * @param destinataires (email séparé par ',')
     */
    public void sendHtmlMessage(String sujet, String texte, String destinataires) throws MessagingException {
        sendMessage(sujet, texte, destinataires, true);

    }

    /**
     * Envoi de mail
     * @param sujet
     * @param texte
     * @param destinataires (email séparé par ',')
     * @param htmlContent true si html siono false
     */
    private void sendMessage(String sujet, String texte, String destinataires, boolean htmlContent) throws MessagingException {
        if(mailSender == null || StringUtils.isEmpty(destinataires)) {
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setSubject(sujet);
        messageHelper.setFrom(mailSenderAddress);
        messageHelper.setTo(InternetAddress.parse(destinataires));
        messageHelper.setText(texte, htmlContent);

//        ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(pieceJointe.getBlob(), pieceJointe.getFileType());
//        messageHelper.addAttachment(pieceJointe.getFileName(), byteArrayDataSource);

        mailSender.send(message);

    }

}
