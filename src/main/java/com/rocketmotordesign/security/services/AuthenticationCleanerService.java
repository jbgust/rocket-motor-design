package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDateTime.now;

@Service
public class AuthenticationCleanerService {

    private final UserValidationTokenRepository userValidationTokenRepository;
    private final UserRepository userRepository;

    public AuthenticationCleanerService(UserValidationTokenRepository userValidationTokenRepository, UserRepository userRepository) {
        this.userValidationTokenRepository = userValidationTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Scheduled(cron = "${authentication.cleaner.cron:0 0 1 * * *}")
    public void clean() {
        userValidationTokenRepository.deleteAllByExpiryDateBefore(now());
        userRepository.deleteAllByCompteValideFalseAndDateCreationBefore(now());
    }
}
