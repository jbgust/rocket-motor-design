package com.rocketmotordesign.admin.service;

import com.rocketmotordesign.admin.MailRequest;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.mail.MessagingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        StopWatch stopWatch = new StopWatch("Envoi newsletter");
        stopWatch.start();
        List<User> users = userRepository.findUserByReceiveNewsletterIsTrueAndCompteValideIsTrueOrderByIdAsc();
        AtomicInteger counter = new AtomicInteger(0);
        users.stream()
                .skip(request.getStart()-1)
                .limit(request.getPageSize())
                .forEach(user -> {
                    try {
                        LOGGER.info("Sending mail {}/{}", counter.incrementAndGet(), request.getPageSize());
                        mailService.sendHtmlMessage(request.getSubject(), request.getHtmlContent(), user.getEmail());
                    } catch (MessagingException e) {
                        LOGGER.error("failed to send newsletter to user("+user.getId()+")", e);
                    }
                });
        stopWatch.stop();
        LOGGER.info(stopWatch.prettyPrint());
    }
}