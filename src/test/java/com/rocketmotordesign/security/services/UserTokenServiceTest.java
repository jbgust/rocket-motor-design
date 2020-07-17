package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.mail.MessagingException;

import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


class UserTokenServiceTest {

    private MailService mailService;
    private UserValidationTokenRepository userValidationTokenRepository;
    private UserTokenService userTokenService;

    @BeforeEach
    void setUp() {
        mailService = mock(MailService.class);
        userValidationTokenRepository = mock(UserValidationTokenRepository.class);
        userTokenService = new UserTokenService(mailService, "http://BaseURL.com", userValidationTokenRepository);
    }

    @Test
    void doitEnvoyerLeMailDeValidation() throws EnvoiLienValidationException, MessagingException {
        //GIVEN
        User utilisateur = new User("jojo@jeje.tz", "pass");

        //WHEN
        userTokenService.envoyerLienValidation(utilisateur);

        //THEN
        ArgumentCaptor<UserValidationToken> argumentCaptor = ArgumentCaptor.forClass(UserValidationToken.class);
        verify(userValidationTokenRepository, times(1)).save(argumentCaptor.capture());

        UserValidationToken validationToken = argumentCaptor.getValue();
        assertThat(validationToken.getUtilisateur()).isEqualTo(utilisateur);
        assertThat(validationToken.getTokenType()).isEqualTo(CREATION_COMPTE);

        verify(mailService, times(1))
                .sendHtmlMessage("METEOR : activate your account",
                        "<html><body><p>Click on the link below to activate your account.</p><" +
                                "a href=\"http://BaseURL.com/auth/validate/" + validationToken.getId() + "\">" +
                                "http://BaseURL.com/auth/validate/" + validationToken.getId() +
                                "</a></body></html>",
                        utilisateur.getEmail());
    }

    @Test
    void envoiUneExceptionSiMailNonEnvoye() throws MessagingException {
        User utilisateur = new User("jojo@jeje.tz", "pass");
        doThrow(new MessagingException()).when(mailService).sendHtmlMessage(anyString(), anyString(), anyString());

        assertThatThrownBy( () -> userTokenService.envoyerLienValidation(utilisateur))
                .isInstanceOf(EnvoiLienValidationException.class);
    }


}
