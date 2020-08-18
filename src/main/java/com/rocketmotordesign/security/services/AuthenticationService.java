package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.UpdatePasswordRequest;
import com.rocketmotordesign.security.models.ERole;
import com.rocketmotordesign.security.models.Role;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.repository.RoleRepository;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.request.ChangePasswordRequest;
import com.rocketmotordesign.security.request.SignupRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;
import static com.rocketmotordesign.security.models.UserValidationTokenType.RESET_PASSWORD;

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

    public void enregistrerUtilisateur(SignupRequest signUpRequest) throws UtilisateurDejaEnregistrerException, EnvoiLienException {

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


    public void envoyerMailChangementPassword(ChangePasswordRequest changePasswordRequest) throws UtilisateurNonTrouve, EnvoiLienException {
        Optional<User> byEmail = userRepository.findByEmail(changePasswordRequest.getEmail());
        if (byEmail.isPresent()) {
            userTokenService.envoyerLienResetPassword(byEmail.get());
        } else {
            throw new UtilisateurNonTrouve();
        }
    }

    public void changePassword(String idToken, UpdatePasswordRequest updatePasswordRequest) throws TokenExpireException, TokenNotFoundExcetpion {

        Optional<UserValidationToken> tokenType = userTokenService.checkToken(idToken, RESET_PASSWORD);
        if(tokenType.isPresent()){
            UserValidationToken userValidationToken = tokenType.get();
            userValidationToken.getUtilisateur().setPassword(encoder.encode(updatePasswordRequest.getPassword()));
            userRepository.save(userValidationToken.getUtilisateur());
            userTokenService.deleteToken(userValidationToken);
        } else {
            throw new TokenNotFoundExcetpion();
        }

    }

    public void validerCompte(String idToken) throws TokenExpireException, TokenNotFoundExcetpion {

        Optional<UserValidationToken> tokenType = userTokenService.checkToken(idToken, CREATION_COMPTE);
        if(tokenType.isPresent()){
            UserValidationToken userValidationToken = tokenType.get();
            userValidationToken.getUtilisateur().setCompteValide(true);
            userRepository.save(userValidationToken.getUtilisateur());
            userTokenService.deleteToken(userValidationToken);
            }  else {
            throw new TokenNotFoundExcetpion();
        }
    }

    public void renvoyerActivation(String idToken) throws EnvoiLienException {
        userTokenService.renvoyerActivation(idToken);
    }
}
