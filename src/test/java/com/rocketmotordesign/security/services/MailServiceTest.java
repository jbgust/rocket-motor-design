package com.rocketmotordesign.security.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
public class MailServiceTest {


    private MailService mailService;

    private JavaMailSender mailSender;

    @Value("classpath:mail/rocket-launch.png")
    Resource logoMailResourceFile;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        mailService = new MailService(mailSender, "sender@domain.org", logoMailResourceFile);
    }

    @Test
    public void doitEnvoyerUnMail() throws MessagingException, IOException {
        // GIVEN
        Session instance = Session.getDefaultInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(instance);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(mailService, "mailSenderAddress", "emetteur@carst.com");

        // WHEN
        mailService.sendMessage("sujet","texte", "test@test.com");

        // THEN
        verify(mailSender).send(mimeMessage);
    }

    @Test
    public void doitEnvoyerUnMailHTML() throws MessagingException, IOException {
        // GIVEN
        Session instance = Session.getDefaultInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(instance);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(mailService, "mailSenderAddress", "emetteur@carst.com");

        // WHEN
        mailService.sendHtmlMessage("sujet","texte", "test@test.com");

        // THEN
        verify(mailSender).send(mimeMessage);
    }

}
