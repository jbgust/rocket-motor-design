package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.models.ERole;
import com.rocketmotordesign.security.models.Role;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.RoleRepository;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.request.SignupRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final UserTokenService userTokenService;


    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder encoder,
                                 RoleRepository roleRepository, UserTokenService userTokenService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.userTokenService = userTokenService;
    }

    public void enregistrerUtilisateur(SignupRequest signUpRequest) throws UtilisateurDejaEnregistrerException, EnvoiLienValidationException {

        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new UtilisateurDejaEnregistrerException();
        }

        // Create new user's account
        User user = new User(signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);
        userTokenService.envoyerLienValidation(user);
    }



}
