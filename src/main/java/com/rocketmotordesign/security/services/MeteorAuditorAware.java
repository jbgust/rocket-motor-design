package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MeteorAuditorAware implements AuditorAware<User> {

    private final UserRepository userRepository;

    public MeteorAuditorAware(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        } else if( authentication.getPrincipal() instanceof User) {
            return Optional.ofNullable((User)authentication.getPrincipal());
        } else {
            return Optional.empty();
        }

    }
}
