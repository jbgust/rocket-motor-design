package com.rocketmotordesign.security;

import com.rocketmotordesign.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
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
    private MockMvc mvc;

    @Test
    void doitEnregistrerUnUtilisateur() throws Exception {
        // WHEN
        ResultActions resultActions = mvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"toto@titi.fr\",\n" +
                        "  \"password\": \"Toto$it1\"\n" +
                        "}"));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully!")));

        assertThat(userRepository.findByEmail("toto@titi.fr"))
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
                .hasValueSatisfying(user -> assertThat(
                        user.getDerniereConnexion())
                        .isBetween(avantConnexion, apresConnexion));
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

}
