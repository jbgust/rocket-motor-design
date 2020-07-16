package com.rocketmotordesign.security;

import com.rocketmotordesign.security.jwt.JwtUtils;
import com.rocketmotordesign.security.models.*;
import com.rocketmotordesign.security.repository.RoleRepository;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import com.rocketmotordesign.security.request.LoginRequest;
import com.rocketmotordesign.security.request.SignupRequest;
import com.rocketmotordesign.security.response.JwtResponse;
import com.rocketmotordesign.security.response.MessageResponse;
import com.rocketmotordesign.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
    AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
    PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserValidationTokenRepository userValidationTokenRepository;

	@Transactional
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		userRepository.logDateConnexion(userDetails.getEmail());

		return ResponseEntity.ok(new JwtResponse(jwt,
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				roles));
	}

	@Transactional
	@PostMapping("/validate/{idToken}")
	public ResponseEntity validationCompte(@PathVariable String idToken) {

		Optional<UserValidationToken> tokenType = userValidationTokenRepository.findByIdAndTokenType(idToken, CREATION_COMPTE);
		if(tokenType.isPresent()){
			UserValidationToken userValidationToken = tokenType.get();
			if(userValidationToken.getExpiryDate().isAfter(LocalDateTime.now())){
				userValidationToken.getUtilisateur().setCompteValide(true);
				userRepository.save(userValidationToken.getUtilisateur());
				return ResponseEntity.ok().build();
			} else {
				return ResponseEntity.badRequest().body("Token has expired.");
			}
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@Transactional
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

		if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
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
		userValidationTokenRepository.save(new UserValidationToken(UUID.randomUUID().toString(), user, CREATION_COMPTE));

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
