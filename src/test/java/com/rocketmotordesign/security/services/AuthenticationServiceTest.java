package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.UpdatePasswordRequest;
import com.rocketmotordesign.security.models.Role;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.repository.RoleRepository;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.request.ChangePasswordRequest;
import com.rocketmotordesign.security.request.SignupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static com.rocketmotordesign.security.models.ERole.ROLE_USER;
import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;
import static com.rocketmotordesign.security.models.UserValidationTokenType.RESET_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    RoleRepository roleRepository;

    @Mock
    UserTokenService userTokenService;

    @Test
    void neDoitPasEnregistrerUnEmailExistant() {
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail("email@domain.cz");
        signUpRequest.setPassword("password");

        given(userRepository.findByEmail(signUpRequest.getEmail())).willReturn(Optional.of(new User()));

        assertThatThrownBy( () -> authenticationService.enregistrerUtilisateur(signUpRequest))
                .isInstanceOf(UtilisateurDejaEnregistrerException.class);

        verifyNoInteractions(userTokenService);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void doitEnregistrerUnUtilisateur() throws UtilisateurDejaEnregistrerException, EnvoiLienException {
        //GIVEN
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail("email@domain.cz");
        signUpRequest.setPassword("password");

        given(userRepository.findByEmail(signUpRequest.getEmail())).willReturn(Optional.empty());
        given(encoder.encode(signUpRequest.getPassword())).willReturn("mdpChiffre");
        Role role = new Role(ROLE_USER);
        given(roleRepository.findByName(ROLE_USER)).willReturn(Optional.of(role));

        //WHEN
        authenticationService.enregistrerUtilisateur(signUpRequest);

        //THEN
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());

        User captorUser = userArgumentCaptor.getValue();
        assertThat(captorUser).extracting(User::getEmail, User::getPassword)
                .containsExactly("email@domain.cz", "mdpChiffre");
        assertThat(captorUser.getRoles())
                .hasSize(1)
                .extracting(Role::getName)
                .containsExactly(ROLE_USER);

        ArgumentCaptor<User> userArgumentCaptor1 = ArgumentCaptor.forClass(User.class);
        verify(userTokenService, times(1)).envoyerLienValidation(userArgumentCaptor1.capture());
        assertThat(userArgumentCaptor1.getValue()).extracting(User::getEmail, User::getPassword)
                .containsExactly("email@domain.cz", "mdpChiffre");
    }

    @Test
    void doitValiderUnCompte() throws TokenExpireException, TokenNotFoundExcetpion {
        //GIVEN
        String idToken = UUID.randomUUID().toString();
        User utilisateur = new User();
        UserValidationToken userValidationToken = new UserValidationToken(idToken, utilisateur, CREATION_COMPTE, 3600);
        given(userTokenService.checkToken(idToken, CREATION_COMPTE))
                .willReturn(Optional.of(userValidationToken));

        //WHEN
        authenticationService.validerCompte(idToken);

        //THEN
        assertThat(utilisateur.isCompteValide()).isTrue();
        verify(userRepository, times(1)).save(utilisateur);
        verify(userTokenService, times(1)).deleteToken(userValidationToken);
    }

    @Test
    void doitIdentifierUnTokenInexistant() throws TokenExpireException {
        //GIVEN
        String idToken = UUID.randomUUID().toString();
        given(userTokenService.checkToken(idToken, CREATION_COMPTE))
                .willReturn(Optional.empty());

        //WHEN
        assertThatThrownBy(() -> authenticationService.validerCompte(idToken))
                .isInstanceOf(TokenNotFoundExcetpion.class);
    }

    @Test
    void doitChangerDePassword() throws TokenExpireException, TokenNotFoundExcetpion {
        //GIVEN
        String idToken = UUID.randomUUID().toString();
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setPassword("NewP@ssw0d!");

        User utilisateur = mock(User.class);
        UserValidationToken userValidationToken = new UserValidationToken(idToken, utilisateur, RESET_PASSWORD, 3600);
        given(userTokenService.checkToken(idToken, RESET_PASSWORD))
                .willReturn(Optional.of(userValidationToken));
        given(encoder.encode("NewP@ssw0d!")).willReturn("newEncryptedPassword");

        //WHEN
        authenticationService.changePassword(idToken, updatePasswordRequest);

        //THEN
        verify(utilisateur, times(1)).setPassword("newEncryptedPassword");
        verify(userRepository, times(1)).save(utilisateur);
        verify(userTokenService, times(1)).deleteToken(userValidationToken);
    }

    @Test
    void doitIdentifierUnTokenResetPasswordInexistant() throws TokenExpireException {
        //GIVEN
        String idToken = UUID.randomUUID().toString();
        given(userTokenService.checkToken(idToken, RESET_PASSWORD))
                .willReturn(Optional.empty());

        //WHEN
        assertThatThrownBy(() -> authenticationService.changePassword(idToken, new UpdatePasswordRequest()))
                .isInstanceOf(TokenNotFoundExcetpion.class);
    }

    @Test
    void doitEnvoyerMailChangementPassword() throws EnvoiLienException, UtilisateurNonTrouve {
        //GIVEN
        User user = new User("e.mail@domain.pl", "pwd");
        given(userRepository.findByEmail("e.mail@domain.pl"))
                .willReturn(Optional.of(user));

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setEmail("e.mail@domain.pl");

        //WHEN
        authenticationService.envoyerMailChangementPassword(changePasswordRequest);

        //THEN
        verify(userTokenService, times(1)).envoyerLienResetPassword(user);
    }
    @Test
    void envoiExceptionSiUtilisateurNonTrouve() throws EnvoiLienException, UtilisateurNonTrouve {
        //GIVEN
        given(userRepository.findByEmail("e.mail@domain.pl"))
                .willReturn(Optional.empty());

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setEmail("e.mail@domain.pl");

        //WHEN
        assertThatThrownBy( () -> authenticationService.envoyerMailChangementPassword(changePasswordRequest))
                .isInstanceOf(UtilisateurNonTrouve.class);

    }

}
