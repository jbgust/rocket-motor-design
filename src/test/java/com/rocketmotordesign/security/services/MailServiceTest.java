package com.rocketmotordesign.security.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender mailSender;

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
