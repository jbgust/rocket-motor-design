package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.models.UserValidationTokenType;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;
import static com.rocketmotordesign.security.models.UserValidationTokenType.RESET_PASSWORD;

@Service
public class UserTokenService {

    private static final Logger logger = LoggerFactory.getLogger(UserTokenService.class);

    private final MailService mailService;
    private final String baseUrl;
    private long tokenValidationExpirationInSeconde;
    private final UserValidationTokenRepository userValidationTokenRepository;

    public UserTokenService(MailService mailService,
                            @Value("${meteor.base-url}") String baseUrl,
                            @Value("${app.userTokenExpirationSeconde}") long tokenValidationExpirationInSeconde,
                            UserValidationTokenRepository userValidationTokenRepository) {
        this.mailService = mailService;
        this.baseUrl = baseUrl;
        this.tokenValidationExpirationInSeconde = tokenValidationExpirationInSeconde;
        this.userValidationTokenRepository = userValidationTokenRepository;
    }

    private String buildValidationEmail(UserValidationToken validationToken, String message) {
        String url = baseUrl+"/validate?token="+validationToken.getId()+"&tokenType="+ validationToken.getTokenType();
        return String.format("<html><body>" +
                "<p>"+ message +"</p>" +
                "<a href=\"%s\">%1$s</a>" +
                "</body></html>", url);
    }

    public void envoyerLienValidation(User utilisateur) throws EnvoiLienException {

        UserValidationToken validationToken = new UserValidationToken(UUID.randomUUID().toString(), utilisateur, CREATION_COMPTE, tokenValidationExpirationInSeconde);
        userValidationTokenRepository.save(validationToken);
        try {
            mailService.sendHtmlMessage("METEOR : activate your account", buildValidationEmail(validationToken, "Click on the link below to activate your account."), utilisateur.getEmail());
        } catch (MessagingException e) {
            logger.error("Echec envoi mail validation pour utilisateur : "+utilisateur.getId(), e);
            throw new EnvoiLienException();
        }
    }

    public void envoyerLienResetPassword(User utilisateur) throws EnvoiLienException {
        UserValidationToken resetToken = new UserValidationToken(UUID.randomUUID().toString(), utilisateur, RESET_PASSWORD, tokenValidationExpirationInSeconde);
        userValidationTokenRepository.save(resetToken);
        try {
            mailService.sendHtmlMessage("METEOR : reset your password", buildValidationEmail(resetToken, "Click on the link below to reset your password."), utilisateur.getEmail());
        } catch (MessagingException e) {
            logger.error("Echec envoi mail validation pour utilisateur : "+utilisateur.getId(), e);
            throw new EnvoiLienException();
        }
    }

    public Optional<UserValidationToken> checkToken(String idToken, UserValidationTokenType tokenType) throws TokenExpireException {
        Optional<UserValidationToken> userToken = userValidationTokenRepository.findByIdAndTokenType(idToken, tokenType);
        if(userToken.isPresent()){
            UserValidationToken userValidationToken = userToken.get();
            if(userValidationToken.getExpiryDate().isBefore(LocalDateTime.now())){
                throw new TokenExpireException();
            }
        }
        return userToken;
    }

    public void deleteToken(UserValidationToken userValidationToken) {
        userValidationTokenRepository.delete(userValidationToken);
    }

    public void renvoyerActivation(String idToken) throws EnvoiLienException {
        Optional<UserValidationToken> oldToken = userValidationTokenRepository.findByIdAndTokenType(idToken, CREATION_COMPTE);
        if(oldToken.isPresent()){
            envoyerLienValidation(oldToken.get().getUtilisateur());
            userValidationTokenRepository.delete(oldToken.get());
        } else {
            throw new IllegalArgumentException("Token ot found");
        }
    }
}
