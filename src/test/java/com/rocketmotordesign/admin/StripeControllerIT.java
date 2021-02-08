package com.rocketmotordesign.admin;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.services.MailService;
import com.stripe.net.Webhook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static com.rocketmotordesign.utils.TestHelper.asString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class StripeControllerIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MailService mailService;

    @Value("${stripe.webhook.signing.secret}")
    private String stripeWebhookSecret;

    @Value("classpath:stripe/customerCreatedRequest.json")
    private Resource customerCreatedRequest;

    @Value("classpath:stripe/chargeSucceededRequest.json")
    private Resource chargeSucceededRequest;

    @Test
    void shouldProcessNewClient() throws Exception {
        //GIVEN
        long timestamp = Webhook.Util.getTimeNow();
        String payload = asString(customerCreatedRequest);


        User customer1 = new User("customer1@test.com", "pwd");
        given(userRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        // WHEN
        ResultActions resultActions = mvc.perform(post("/stripe/events")
                .header("stripe-signature", "t=" + timestamp + ",v1=" +  buildStripeSignature(payload, timestamp) + ",v0=3045dd656a5494866adbfa1e74b4d3470a360174d8336714bb41485a8abd327d")
                .contentType(APPLICATION_JSON)
                .content(payload));

        // THEN
        resultActions.andExpect(status().isOk());
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1))
                .save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue())
                .extracting(User::getEmail, User::isDonator)
                .containsExactly("customer1@test.com", true);

    }

    @Test
    void shouldProcessNewCharge() throws Exception {
        //GIVEN
        long timestamp = Webhook.Util.getTimeNow();
        String payload = asString(chargeSucceededRequest);

        User customer1 = new User("customer1@test.com", "pwd");
        given(userRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        // WHEN
        ResultActions resultActions = mvc.perform(post("/stripe/events")
                .header("stripe-signature", "t=" + timestamp + ",v1=" +  buildStripeSignature(payload, timestamp) + ",v0=3045dd656a5494866adbfa1e74b4d3470a360174d8336714bb41485a8abd327d")
                .contentType(APPLICATION_JSON)
                .content(payload));

        // THEN
        resultActions.andExpect(status().isOk());
        verify(mailService, times(1))
                .sendHtmlMessage(
                        "METEOR : New donation",
                        "You receive a new donation of 1.00$ from  unknow customer",
                        "meteor@open-sky.fr");

    }

    private String buildStripeSignature(String payload, long timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        String signedPayload = String.format("%d.%s", timestamp, payload);
        return Webhook.Util.computeHmacSha256(stripeWebhookSecret, signedPayload);
    }

}