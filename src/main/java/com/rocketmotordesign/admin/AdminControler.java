package com.rocketmotordesign.admin;

import com.rocketmotordesign.security.services.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController()
@RequestMapping("admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminControler {

    private final MailService mailService;
    private final String adminMail;

    public AdminControler(MailService mailService, @Value("${mail.sender}") String adminMail) {
        this.mailService = mailService;
        this.adminMail = adminMail;
    }

    @PostMapping("send-mail")
    public void senMail(@RequestBody MailRequest request) throws MessagingException {
        mailService.sendHtmlMessage(request.getSubject(), request.getHtmlContent(), adminMail);
    }
}
