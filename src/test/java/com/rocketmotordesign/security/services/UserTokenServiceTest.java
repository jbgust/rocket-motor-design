package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.models.UserValidationTokenType;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.mail.MessagingException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;
import static com.rocketmotordesign.security.models.UserValidationTokenType.RESET_PASSWORD;
import static com.rocketmotordesign.utils.TestHelper.buildExpectedMail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserTokenServiceTest {

    private MailService mailService;
    private UserValidationTokenRepository userValidationTokenRepository;
    private UserTokenService userTokenService;

    @Value("classpath:mail/mailModel.html")
    Resource mailModelResourceFile;

    @BeforeEach
    void setUp() {
        mailService = mock(MailService.class);
        userValidationTokenRepository = mock(UserValidationTokenRepository.class);
        userTokenService = new UserTokenService(mailService, "http://BaseURL.com", 3600, mailModelResourceFile, userValidationTokenRepository);
    }

    @Test
    void doitEnvoyerLeMailDeValidation() throws EnvoiLienException, MessagingException {
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

        String url = buildUrlValidation(validationToken, CREATION_COMPTE);

        ArgumentCaptor<String> mailContentArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService, times(1))
                .sendHtmlMessage(eq("METEOR : activate your account"),
                        mailContentArgumentCaptor.capture(),
                        eq(utilisateur.getEmail()));
        assertThat(mailContentArgumentCaptor.getValue())
                .isEqualTo(buildExpectedMail("Welcome to METEOR", "Click on the link below to activate your account.", "http://BaseURL.com", url));
    }

    @Test
    void doitRenvoyerLeMailDeValidation() throws EnvoiLienException, MessagingException {
        //GIVEN
        User utilisateur = new User("jojo@jeje.tz", "pass");
        UserValidationToken ancienToken = new UserValidationToken(UUID.randomUUID().toString(), utilisateur, CREATION_COMPTE, 1);
        given(userValidationTokenRepository.findByIdAndTokenType(ancienToken.getId(), CREATION_COMPTE))
                .willReturn(Optional.of(ancienToken));

        //WHEN
        userTokenService.renvoyerActivation(ancienToken.getId());

        //THEN
        ArgumentCaptor<UserValidationToken> argumentCaptor = ArgumentCaptor.forClass(UserValidationToken.class);
        verify(userValidationTokenRepository, times(1)).save(argumentCaptor.capture());
        verify(userValidationTokenRepository, times(1)).delete(ancienToken);

        UserValidationToken validationToken = argumentCaptor.getValue();
        assertThat(validationToken.getUtilisateur()).isEqualTo(utilisateur);
        assertThat(validationToken.getTokenType()).isEqualTo(CREATION_COMPTE);

        String url = buildUrlValidation(validationToken, CREATION_COMPTE);
        ArgumentCaptor<String> mailContentArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService, times(1))
                .sendHtmlMessage(eq("METEOR : activate your account"),
                        mailContentArgumentCaptor.capture(),
                        eq(utilisateur.getEmail()));
        assertThat(mailContentArgumentCaptor.getValue())
                .isEqualTo(buildExpectedMail("Welcome to METEOR", "Click on the link below to activate your account.", "http://BaseURL.com", url));
    }

    @Test
    void doitEnvoyerExceptionSiTokenInexistant() {
        //GIVEN
        String fauxId = UUID.randomUUID().toString();
        given(userValidationTokenRepository.findByIdAndTokenType(fauxId, CREATION_COMPTE))
                .willReturn(Optional.empty());

        //WHEN
        assertThatThrownBy(() -> userTokenService.renvoyerActivation(fauxId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void envoiUneExceptionSiMailNonEnvoye() throws MessagingException {
        User utilisateur = new User("jojo@jeje.tz", "pass");
        doThrow(new MessagingException()).when(mailService).sendHtmlMessage(anyString(), anyString(), anyString());

        assertThatThrownBy( () -> userTokenService.envoyerLienValidation(utilisateur))
                .isInstanceOf(EnvoiLienException.class);
    }

    @Test
    void doitEnvoyerLeMailDeReset() throws EnvoiLienException, MessagingException {
        //GIVEN
        User utilisateur = new User("jojo@jeje.tz", "pass");

        //WHEN
        userTokenService.envoyerLienResetPassword(utilisateur);

        //THEN
        ArgumentCaptor<UserValidationToken> argumentCaptor = ArgumentCaptor.forClass(UserValidationToken.class);
        verify(userValidationTokenRepository, times(1)).save(argumentCaptor.capture());

        UserValidationToken validationToken = argumentCaptor.getValue();
        assertThat(validationToken.getUtilisateur()).isEqualTo(utilisateur);
        assertThat(validationToken.getTokenType()).isEqualTo(RESET_PASSWORD);

        String url = buildUrlValidation(validationToken, RESET_PASSWORD);

        ArgumentCaptor<String> mailContentArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService, times(1))
                .sendHtmlMessage(eq("METEOR : reset your password"),
                        mailContentArgumentCaptor.capture(),
                        eq(utilisateur.getEmail()));
        assertThat(mailContentArgumentCaptor.getValue())
                .isEqualTo(buildExpectedMail("METEOR", "Click on the link below to reset your password.", "http://BaseURL.com", url));
    }

    @Test
    void envoiUneExceptionSiMailResetNonEnvoye() throws MessagingException {
        User utilisateur = new User("jojo@jeje.tz", "pass");
        doThrow(new MessagingException()).when(mailService).sendHtmlMessage(anyString(), anyString(), anyString());

        assertThatThrownBy( () -> userTokenService.envoyerLienResetPassword(utilisateur))
                .isInstanceOf(EnvoiLienException.class);
    }

    @Test
    void doitVerfierUnToken() throws TokenExpireException {
        String idToken = UUID.randomUUID().toString();
        UserValidationToken userValidationToken = mock(UserValidationToken.class);
        when(userValidationToken.getExpiryDate()).thenReturn(LocalDateTime.now().plusHours(1));

        given(userValidationTokenRepository.findByIdAndTokenType(idToken, CREATION_COMPTE))
                .willReturn(Optional.of(userValidationToken));

        //WHEN
        Optional<UserValidationToken> optional = userTokenService.checkToken(idToken, CREATION_COMPTE);

        //THEN
        assertThat(optional)
                .isPresent()
                .hasValue(userValidationToken);
    }

    @Test
    void envoiUneExceptionSiTokenExpire() {
        String idToken = UUID.randomUUID().toString();
        UserValidationToken userValidationToken = mock(UserValidationToken.class);
        when(userValidationToken.getExpiryDate()).thenReturn(LocalDateTime.now());

        given(userValidationTokenRepository.findByIdAndTokenType(idToken, CREATION_COMPTE))
                .willReturn(Optional.of(userValidationToken));

        //WHEN
        assertThatThrownBy( () -> userTokenService.checkToken(idToken, CREATION_COMPTE))
                .isInstanceOf(TokenExpireException.class);

    }

    private String buildUrlValidation(UserValidationToken validationToken, UserValidationTokenType tokenType) {
        return "http://BaseURL.com/validate?token=" + validationToken.getId() + "&tokenType=" + tokenType;
    }
}
