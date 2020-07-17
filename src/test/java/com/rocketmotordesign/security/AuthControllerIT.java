package com.rocketmotordesign.security;

import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import com.rocketmotordesign.security.services.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.stream.StreamSupport;

import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIT {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserValidationTokenRepository userValidationTokenRepository;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MailService mailService;

    @Test
    void doitEnregistrerUnUtilisateur() throws Exception {
        // WHEN
        ResultActions resultActions = mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"tata@titi.fr\",\n" +
                        "  \"password\": \"Toto$it1\"\n" +
                        "}"));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully!")));

        assertThat(userRepository.findByEmail("tata@titi.fr"))
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user.getDateCreation().toLocalDate()).isToday());
    }

    @Test
    void neDoitPasEnregistrerUnUtilisateurSiPasswordSimple() throws Exception {
        // WHEN
        ResultActions resultActions = mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"toto@titi.fr\",\n" +
                        "  \"password\": \"ototiti\"\n" +
                        "}"));

        //THEN
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitSeConntecter() throws Exception {
        // WHEN
        mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"toto@titi.fr\",\n" +
                        "  \"password\": \"Toto$it1\"\n" +
                        "}"));

        validerCompte("toto@titi.fr");

        LocalDateTime avantConnexion = LocalDateTime.now();
        ResultActions resultActions = mvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"username\": \"toto@titi.fr\",\n" +
                        "  \"password\": \"Toto$it1\"\n" +
                        "}"));
        LocalDateTime apresConnexion = LocalDateTime.now();

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("toto@titi.fr")));

        assertThat(userRepository.findByEmail("toto@titi.fr"))
                .isPresent()
                .hasValueSatisfying(user -> {
                        assertThat(user.getDerniereConnexion()).isBetween(avantConnexion, apresConnexion);
                        assertThat(user.isCompteValide()).isTrue();
                });
    }

    @Test
    void neDoitPasSeConnecterSiCompteNonValider() throws Exception {
        // WHEN
        mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"toto@titi.fr\",\n" +
                        "  \"password\": \"Toto$it1\"\n" +
                        "}"));

        ResultActions resultActions = mvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"username\": \"toto@titi.fr\",\n" +
                        "  \"password\": \"Toto$it1\"\n" +
                        "}"));

        //THEN
        resultActions
                .andExpect(status().isUnauthorized());
    }

    @Test
    void doitEchoueSiRequeteIncomplete() throws Exception {
        // WHEN
        ResultActions resultActions = mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"toto@titi.fr\"\n" +
                        "}"));

        //THEN
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitEchoueSiEmailInvalide() throws Exception {
        // WHEN
        ResultActions resultActions = mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"titi.fr\",\n" +
                        "  \"password\": \"tototiti\"\n" +
                        "}"));

        //THEN
        resultActions
                .andExpect(status().isBadRequest());
    }

    private String recupererTokenValidationCompte(String email) {
        return StreamSupport.stream(userValidationTokenRepository.findAll().spliterator(), false)
                .filter(userValidationToken -> userValidationToken.getUtilisateur().getEmail().equals(email))
                .filter(userValidationToken -> CREATION_COMPTE == userValidationToken.getTokenType())
                .map(UserValidationToken::getId)
                .findFirst().orElse(null);
    }

    private void validerCompte(String email) throws Exception {
        String tokenValidationCompte = recupererTokenValidationCompte(email);

        mvc.perform(get("/auth/validate/{idToken}", tokenValidationCompte)
                .contentType(MediaType.APPLICATION_JSON));
    }

}
