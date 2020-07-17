package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.UUID;

import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;

@Service
public class UserTokenService {

    private static final Logger logger = LoggerFactory.getLogger(UserTokenService.class);

    private final MailService mailService;
    private final String baseUrl;
    private final UserValidationTokenRepository userValidationTokenRepository;

    public UserTokenService(MailService mailService,
                            @Value("${application.base-url}") String baseUrl, UserValidationTokenRepository userValidationTokenRepository) {
        this.mailService = mailService;
        this.baseUrl = baseUrl;
        this.userValidationTokenRepository = userValidationTokenRepository;
    }

    private String buildValidationEmail(UserValidationToken validationToken) {
        String url = baseUrl+"/auth/validate/"+validationToken.getId();
        return String.format("<html><body>" +
                "<p>Click on the link below to activate your account.</p>" +
                "<a href=\"%s\">%1$s</a>" +
                "</body></html>", url);
    }

    public void envoyerLienValidation(User utilisateur) throws EnvoiLienValidationException {

        UserValidationToken validationToken = new UserValidationToken(UUID.randomUUID().toString(), utilisateur, CREATION_COMPTE);
        userValidationTokenRepository.save(validationToken);
        try {
            mailService.sendHtmlMessage("METEOR : activate your account", buildValidationEmail(validationToken), utilisateur.getEmail());
        } catch (MessagingException e) {
            logger.error("Echec envoi mail validation pour utilisateur : "+utilisateur.getId(), e);
            throw new EnvoiLienValidationException();
        }
    }
}
