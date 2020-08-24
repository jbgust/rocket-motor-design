package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDateTime.now;

@Service
public class AuthenticationCleanerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationCleanerService.class);

    private final UserValidationTokenRepository userValidationTokenRepository;
    private final UserRepository userRepository;
    private Integer dayBeforeCleaningToken;

    public AuthenticationCleanerService(UserValidationTokenRepository userValidationTokenRepository,
                                        UserRepository userRepository,
                                        @Value("${authentication.cleaner.dayBeforeCleaningToken:15}") Integer dayBeforeCleaningToken) {
        this.userValidationTokenRepository = userValidationTokenRepository;
        this.userRepository = userRepository;
        this.dayBeforeCleaningToken = dayBeforeCleaningToken;
    }

    @Transactional
    @Scheduled(cron = "${authentication.cleaner.cron:0 0 1 * * *}")
    public void clean() {
        int nbTokenSupprimes = userValidationTokenRepository.deleteAllByExpiryDateBefore(now().minusDays(dayBeforeCleaningToken));
        LOGGER.info("{} token(s) supprime(s)", nbTokenSupprimes);
        cleanUsers();
    }

    private void cleanUsers() {
        userRepository.getUsersNonValideSansToken().forEach(user -> {
            LOGGER.info("Suppression utilisateur id={}", user.getId());
            userRepository.delete(user);
        });
    }
}
