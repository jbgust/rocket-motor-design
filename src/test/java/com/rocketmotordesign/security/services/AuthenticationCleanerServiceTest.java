package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.time.LocalDateTime;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class AuthenticationCleanerServiceTest {

    @Test
    void doitNettoyerLesDonnees() {
        //GIVEN
        UserValidationTokenRepository userValidationTokenRepository = mock(UserValidationTokenRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        Integer dayBeaforeCleaningToken = 2;
        AuthenticationCleanerService authenticationCleanerService = new AuthenticationCleanerService(userValidationTokenRepository, userRepository, dayBeaforeCleaningToken);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        given(userRepository.getUsersNonValideSansToken())
                .willReturn(asList(user1, user2));

        //WHEN
        authenticationCleanerService.clean();

        //THEN
        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        InOrder inOrder = inOrder(userValidationTokenRepository, userRepository);
        inOrder.verify(userValidationTokenRepository).deleteAllByExpiryDateBefore(captor.capture());
        inOrder.verify(userRepository).getUsersNonValideSansToken();
        inOrder.verify(userRepository, times(2)).delete(userArgumentCaptor.capture());

        assertThat(captor.getAllValues())
                .hasSize(1)
                .extracting(LocalDateTime::toLocalDate)
                .containsOnly(now().minusDays(dayBeaforeCleaningToken));

        assertThat(userArgumentCaptor.getAllValues())
                .hasSize(2)
                .containsOnly(user1, user2);
    }

}
