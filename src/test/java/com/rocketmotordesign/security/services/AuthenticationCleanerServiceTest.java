package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.time.LocalDateTime;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthenticationCleanerServiceTest {

    @Test
    void doitNettoyerLesDonnees() {
        //GIVEN
        UserValidationTokenRepository userValidationTokenRepository = mock(UserValidationTokenRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        AuthenticationCleanerService authenticationCleanerService = new AuthenticationCleanerService(userValidationTokenRepository, userRepository);

        //WHEN
        authenticationCleanerService.clean();

        //THEN
        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        InOrder inOrder = inOrder(userValidationTokenRepository, userRepository);

        inOrder.verify(userValidationTokenRepository).deleteAllByExpiryDateBefore(captor.capture());
        inOrder.verify(userRepository).deleteAllByCompteValideFalseAndDateCreationBefore(captor.capture());

        assertThat(captor.getAllValues())
                .hasSize(2)
                .extracting(LocalDateTime::toLocalDate)
                .containsOnly(now());
    }

}
