package com.rocketmotordesign.admin.controller;

import com.rocketmotordesign.admin.MailRequest;
import com.rocketmotordesign.admin.service.NewsletterService;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.services.IMailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;

@RestController()
@RequestMapping("admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final IMailService mailService;
    private final String adminMail;
    private boolean enableTestAlertes;
    private final UserRepository userRepository;
    private final NewsletterService newsletterService;

    public AdminController(IMailService mailService,
                           @Value("${mail.sender}") String adminMail,
                           @Value("${enable.test.alertes:false}") boolean enableTestAlertes,
                           UserRepository userRepository,
                           NewsletterService newsletterService) {
        this.mailService = mailService;
        this.adminMail = adminMail;
        this.enableTestAlertes = enableTestAlertes;
        this.userRepository = userRepository;
        this.newsletterService = newsletterService;
    }

    @PostMapping("send-mail")
    public void senMail(@RequestBody MailRequest request) throws MessagingException {
        mailService.sendHtmlMessage(request.getSubject(), request.getHtmlContent(), request.getReceiver());
    }

    @PostMapping("newsletter")
    public void newsletter(@RequestBody MailRequest request) {
        newsletterService.sendNewsletter(request);
    }

    @GetMapping("/tests/alert")
    public ResponseEntity<String> testAlert() {
        if(enableTestAlertes) {
            throw new IllegalArgumentException("Test alerte Logback");
        }
        return ResponseEntity.ok("test alertes not enabled");
    }
}
