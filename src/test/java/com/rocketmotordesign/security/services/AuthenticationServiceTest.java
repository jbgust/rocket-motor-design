package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.models.Role;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.RoleRepository;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.request.SignupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.rocketmotordesign.security.models.ERole.ROLE_USER;
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
    void doitEnregistrerUnUtilisateur() throws UtilisateurDejaEnregistrerException, EnvoiLienValidationException {
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
}
