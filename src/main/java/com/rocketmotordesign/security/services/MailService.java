package com.rocketmotordesign.security.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
@Profile("!dev")
public class MailService implements IMailService {

    private static final String LOGO_METEOR_IMG_CONTENT_ID = "logoMeteorImg";

    private final String mailSenderAddress;
    private final Resource logoMailResourceFile;
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender,
                       @Value("${mail.sender}") String mailSenderAddress,
                       @Value("classpath:mail/rocket-launch.png") Resource logoMailResourceFile) {
        this.mailSender = mailSender;
        this.mailSenderAddress = mailSenderAddress;
        this.logoMailResourceFile = logoMailResourceFile;
    }

    /**
     * Envoi de mail
     * @param sujet
     * @param texte
     * @param destinataires (email séparé par ',')
     */
    @Override
    public void sendMessage(String sujet, String texte, String destinataires) throws MessagingException {
        sendMessage(sujet, texte, destinataires, false);

    }

    /**
     * Envoi de mail
     * @param sujet
     * @param texte
     * @param destinataires (email séparé par ',')
     */
    @Override
    public void sendHtmlMessage(String sujet, String texte, String destinataires) throws MessagingException {
        sendMessage(sujet, texte, destinataires, true);

    }

    /**
     * Envoi de mail
     * @param sujet
     * @param texte
     * @param destinataires (email séparé par ',')
     * @param htmlContent true si html sinon false
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
        messageHelper.addInline(LOGO_METEOR_IMG_CONTENT_ID, logoMailResourceFile);

        mailSender.send(message);

    }

}
