package com.rocketmotordesign.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmotordesign.security.services.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(value = "spring", roles = "ADMIN")
class AdminControlerIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MailService mailService;

    @Value("${mail.sender}")
    String meteorAdminMail;

    private ObjectMapper jsonObjectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Test
    void shouldSendEmailToAdmin() throws Exception {
        // GIVEN
        MailRequest mailRequest = new MailRequest("mon sujet", "<html><body>test</body></html>");
        String request = jsonObjectMapper.writeValueAsString(mailRequest);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/admin/send-mail")
                .contentType(APPLICATION_JSON)
                .content(request));

        // THEN
        resultActions
                .andExpect(status().isOk());

        verify(mailService, times(1))
                .sendHtmlMessage(mailRequest.getSubject(), mailRequest.getHtmlContent(), meteorAdminMail);
    }

}