package com.rocketmotordesign.security;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.models.UserValidationTokenType;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.repository.UserValidationTokenRepository;
import com.rocketmotordesign.security.services.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static com.rocketmotordesign.security.models.UserValidationTokenType.RESET_PASSWORD;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void doitPouvoirChangerDeMotDePasse() throws Exception {
        //Creation du compte
        mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"user@titi.fr\",\n" +
                        "  \"password\": \"User$it1\"\n" +
                        "}"));

        validerCompte("user@titi.fr");

        //demande de changepment de password
        mvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"user@titi.fr\"\n" +
                        "}"))
                .andExpect(status().isOk());

        String tokenResetPassword = recupererToken("user@titi.fr", RESET_PASSWORD);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService, times(2)).sendHtmlMessage(
                startsWith("METEOR : "),
                argumentCaptor.capture(),
                eq("user@titi.fr")
        );

        String urlRenew = "http://test.meteor.gov/validate?token=" + tokenResetPassword + "&tokenType=RESET_PASSWORD";
        assertThat(argumentCaptor.getAllValues().get(1))
                .isEqualTo("<html><body><p>Click on the link below to reset your password.</p>" +
                        "<a href=\"" + urlRenew + "\">" + urlRenew + "</a></body></html>");


        //Changement de password
        mvc.perform(post("/auth/reset-password/{token}", tokenResetPassword)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"password\": \"NewP@ssw0d!\"\n" +
                        "}"))
                .andExpect(status().isOk());

        mvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"username\": \"user@titi.fr\",\n" +
                        "  \"password\": \"NewP@ssw0d!\"\n" +
                        "}"))

                .andExpect(status().isOk());
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

    @Test
    void doitRenvoyerUnLienActivation() throws Exception {
        //GIVEN
        User user = userRepository.save(new User("validation@test.fr", "passwd"));
        UserValidationToken tokenExpire = userValidationTokenRepository.save(new UserValidationToken(randomUUID().toString(), user, CREATION_COMPTE, 0));

        mvc.perform(post("/auth/validate/{idToken}", tokenExpire.getId()))
                .andExpect(status().isBadRequest())
                // Attention on se base sur ce message pour afficher le bouton
                // de renvoi du lien d'activation sur METEOR
                .andExpect(jsonPath("$.message", is("Token has expired.")));

        //WHEN
        ResultActions resultActions = mvc.perform(post("/auth/resent-activation/{idToken}", tokenExpire.getId()));

        //THEN
        resultActions.andExpect(status().isOk());

        assertThat(userValidationTokenRepository.findByIdAndTokenType(tokenExpire.getId(), CREATION_COMPTE))
                .isNotPresent();

        assertThat(recupererToken(user.getEmail(), CREATION_COMPTE)).isNotNull();
    }

    private String recupererToken(String email, UserValidationTokenType tokenType) {
        return StreamSupport.stream(userValidationTokenRepository.findAll().spliterator(), false)
                .filter(userValidationToken -> userValidationToken.getUtilisateur().getEmail().equals(email))
                .filter(userValidationToken -> tokenType == userValidationToken.getTokenType())
                .map(UserValidationToken::getId)
                .findFirst().orElse(null);
    }

    private void validerCompte(String email) throws Exception {
        String tokenValidationCompte = recupererToken(email, CREATION_COMPTE);

        mvc.perform(post("/auth/validate/{idToken}", tokenValidationCompte)
                .contentType(MediaType.APPLICATION_JSON));
    }

}
