package com.rocketmotordesign.admin.service;

import com.rocketmotordesign.admin.MailRequest;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class NewsletterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterService.class);

    private final UserRepository userRepository;
    private final MailService mailService;

    public NewsletterService(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    public void sendNewsletter(MailRequest request) {

        userRepository.findUserByReceiveNewsletterIsTrueAndCompteValideIsTrue().forEach(user -> {
            try {
                mailService.sendHtmlMessage(request.getSubject(), request.getHtmlContent(), user.getEmail());
            } catch (MessagingException e) {
                LOGGER.error("failed to send newsletter to user("+user.getId()+")", e);
            }
        });
    }
}