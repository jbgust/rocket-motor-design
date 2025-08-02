package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    UserRepository userRepository;

    @Test
    void DoitEnvoyerExceptionSiUtilisateurBanni() {
        User banUser = new User();
        banUser.setId(1L);
        banUser.setBan(true);
        given(userRepository.findByEmail("ban@user.fr"))
                .willReturn(Optional.of(banUser));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("ban@user.fr"))
                .isInstanceOf(BanUserException.class)
                .hasMessage("You have been banned due to inappropriate behavior.");

    }

}
