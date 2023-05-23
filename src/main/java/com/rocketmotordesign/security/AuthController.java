package com.rocketmotordesign.security;

import com.rocketmotordesign.security.jwt.JwtUtils;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.request.ChangePasswordRequest;
import com.rocketmotordesign.security.request.LoginRequest;
import com.rocketmotordesign.security.request.SignupRequest;
import com.rocketmotordesign.security.response.JwtResponse;
import com.rocketmotordesign.security.response.MessageResponse;
import com.rocketmotordesign.security.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtUtils jwtUtils;
	private final AuthenticationService authenticationService;

	public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, JwtUtils jwtUtils, AuthenticationService authenticationService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.jwtUtils = jwtUtils;
		this.authenticationService = authenticationService;
	}


	@Transactional
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		User userDetails = (User) authentication.getPrincipal();

		userRepository.logDateConnexion(userDetails.getEmail());

		return ResponseEntity.ok(new JwtResponse(jwt));
	}

	@Transactional
	@PostMapping("/validate/{idToken}")
	public ResponseEntity validationCompte(@PathVariable String idToken) {
		try {
			authenticationService.validerCompte(idToken);
			return ResponseEntity.ok().build();
		} catch (TokenExpireException e) {
			return ResponseEntity.badRequest().body(new MessageResponse("Token has expired."));
		} catch (TokenNotFoundExcetpion tokenNotFoundExcetpion) {
			return ResponseEntity.notFound().build();
		}
	}

	@Transactional
	@PostMapping("/resent-activation/{idToken}")
	public ResponseEntity resenActivationLink(@PathVariable String idToken) {
		try {
			authenticationService.renvoyerActivation(idToken);
			return ResponseEntity.ok().build();
		} catch (EnvoiLienException e) {
			return ResponseEntity.status(INTERNAL_SERVER_ERROR)
					.body(new MessageResponse("Failed to send activation link"));
		}
	}

	@Transactional
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		try {
			authenticationService.enregistrerUtilisateur(signUpRequest);
			return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
		} catch (UtilisateurDejaEnregistrerException e) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		} catch (EnvoiLienException e) {
			return ResponseEntity.status(INTERNAL_SERVER_ERROR)
					.body(new MessageResponse("Failed to send activation link"));
		}
	}

	@Transactional
	@PostMapping("/reset-password")
	public ResponseEntity demandeRenouvellementPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
		try {
			authenticationService.envoyerMailChangementPassword(changePasswordRequest);
			return ResponseEntity.ok(new MessageResponse("Reset link sent successfully!"));
		} catch (UtilisateurNonTrouve utilisateurNonTrouve) {
			return ResponseEntity.notFound().build();
		} catch (EnvoiLienException e) {
			return ResponseEntity.status(INTERNAL_SERVER_ERROR)
					.body(new MessageResponse("Failed to send reset link"));
		}
	}

	@Transactional
	@PostMapping("/reset-password/{idToken}")
	public ResponseEntity modifierPassword(
			@PathVariable String idToken,
			@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
		try {
			authenticationService.changePassword(idToken, updatePasswordRequest);
			return ResponseEntity.ok().build();
		} catch (TokenExpireException e) {
			return ResponseEntity.badRequest().body(new MessageResponse("Token has expired."));
		} catch (TokenNotFoundExcetpion tokenNotFoundExcetpion) {
			return ResponseEntity.notFound().build();
		}
	}
}
