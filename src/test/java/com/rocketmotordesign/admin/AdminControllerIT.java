package com.rocketmotordesign.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.services.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Random;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(value = "spring", roles = "ADMIN")
class AdminControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private MailService mailService;

    private ObjectMapper jsonObjectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Test
    void shouldSendEmailToAdmin() throws Exception {
        // GIVEN
        withUser("user1@domain.org", true);
        withUser("user2@haha.fr", true);
        withUserNotValid("notValid@domain.org", true);
        withUser("nonewsletter@domain.org", false);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/admin/send-mail")
                .contentType(APPLICATION_JSON)
                .content("{\n" +
                        "  \"receiver\": \"receiver@test.info\",\n" +
                        "  \"subject\": \"mon sujet\",\n" +
                        "  \"htmlContent\": \"<html><body>test</body></html>\"\n" +
                        "}"));

        // THEN
        resultActions.andExpect(status().isOk());

        verify(mailService, times(1))
                .sendHtmlMessage("mon sujet", "<html><body>test</body></html>", "receiver@test.info");
    }

    @Test
    void shouldSendEmailToUsers() throws Exception {
        // GIVEN
        MailRequest mailRequest = new MailRequest("mon sujet", "<html><body>test</body></html>", null, 1, 3);
        String request = jsonObjectMapper.writeValueAsString(mailRequest);
        createValidUser("user3@domain.org", true);
        createValidUser("user4@haha.fr", true);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/admin/newsletter")
                .contentType(APPLICATION_JSON)
                .content(request));

        // THEN
        resultActions.andExpect(status().isOk());

        ArgumentCaptor<String> receivers = ArgumentCaptor.forClass(String.class);
        verify(mailService, times(2))
                .sendHtmlMessage(eq(mailRequest.getSubject()), eq(mailRequest.getHtmlContent()), receivers.capture());
    }

    private void createValidUser(String email, boolean newsletter) {
        User user = new User(email, "pwd");
        user.setReceiveNewsletter(newsletter);
        user.setCompteValide(true);
        userRepository.save(user);
    }

    private void withUser(String email, boolean receiveNewsletter) {
        User user = new User(email, "pwd-"+new Random().nextInt());
        user.setReceiveNewsletter(receiveNewsletter);
        userRepository.save(user);
    }

    private void withUserNotValid(String email, boolean receiveNewsletter) {
        User user = new User(email, "pwd-"+new Random().nextInt());
        user.setReceiveNewsletter(receiveNewsletter);
        userRepository.save(user);
    }
}
