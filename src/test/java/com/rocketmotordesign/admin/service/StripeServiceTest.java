package com.rocketmotordesign.admin.service;

import com.rocketmotordesign.admin.controller.DonatelyController;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.services.MailService;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.MessagingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    @InjectMocks
    private StripeService stripeService;

    @Mock
    private DonatelyController donatelyController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stripeService, "mailAlertReceiver", "meteor@open-sky.fr");
    }

    @Test
    void shoulMarkNewCustomerAsDonator() {
        //GIVEN
        User user = new User("new-donator@stripe.com", "pwd");
        Customer customer = mock(Customer.class);
        given(customer.getId()).willReturn(user.getEmail(), "StripeCustomerID");
        given(userRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(user));

        //WHEN
        stripeService.registerNewDonator(customer);

        //THEN
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1))
                .save(userArgumentCaptor.capture());
        User donator = userArgumentCaptor.getValue();
        assertThat(donator)
                .extracting(User::getStripeCustomerId, User::isDonator, User::getLastDonation)
                .containsExactly("StripeCustomerID", false, null);
    }

    @Test
    void shoulSendMailOnNewDonationFromCharge() throws MessagingException {
        //GIVEN
        Charge charge = buildNewCharge("cus_IyFWZ6cmcXsZEY");

        given(userRepository.findByStripeCustomerId("cus_IyFWZ6cmcXsZEY"))
                .willReturn(Optional.of(new User("customer1@test.com", "pwd")));

        //WHEN
        stripeService.handleNewDonation(charge);

        //THEN
        verify(mailService, times(1))
                .sendHtmlMessage("METEOR : New donation", "You receive a new donation of 20.59$ from customer1@test.com", "meteor@open-sky.fr");
    }

    @Test
    void shoulNotMarkNewCustomerWhenEmailNotFound() {
        //GIVEN
        User user = new User("new-donator@stripe.com", "pwd");
        Customer customer = mock(Customer.class);
        given(customer.getEmail()).willReturn(user.getEmail());
        given(userRepository.findByEmail(customer.getEmail()))
                .willReturn(empty());

        //WHEN
        stripeService.registerNewDonator(customer);

        //THEN
        verify(userRepository, never())
                .save(any());
    }

    @Test
    void shoulAddLastDonationDateOnNewDonationFromCharge() throws MessagingException {
        //GIVEN
        LocalDateTime startOfTest = now();
        Charge charge = buildNewCharge("cus_IyFWZ6cmcXsZEY");

        given(userRepository.findByStripeCustomerId("cus_IyFWZ6cmcXsZEY"))
                .willReturn(Optional.of(new User("customer1@test.com", "pwd")));

        //WHEN
        stripeService.handleNewDonation(charge);

        //THEN
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1))
                .save(userArgumentCaptor.capture());

        User donator = userArgumentCaptor.getValue();
        assertThat(donator)
                .extracting(User::getEmail, User::isDonator)
                .containsExactly("customer1@test.com", false);

        assertThat(userArgumentCaptor.getValue().getLastDonation()).isAfter(startOfTest);
        assertThat(userArgumentCaptor.getValue().isActiveDonator(Duration.ofSeconds(10), now())).isTrue();
    }

    private static Charge buildNewCharge(String cus_IyFWZ6cmcXsZEY) {
        Charge charge = mock(Charge.class);

        given(charge.getCustomer()).willReturn(cus_IyFWZ6cmcXsZEY);
        given(charge.getAmount()).willReturn(2059L);
        return charge;
    }

    @Test
    void shoulNotAddLastDonationDateOnNewDonationFromChargeWhenUserNotFound() throws MessagingException {
        //GIVEN
        Charge charge = buildNewCharge("unknowCustomerId");
        given(userRepository.findByStripeCustomerId("unknowCustomerId"))
                .willReturn(empty());

        //WHEN
        stripeService.handleNewDonation(charge);

        //THEN
        verify(mailService, times(1))
                .sendHtmlMessage("METEOR : New donation", "You receive a new donation of 20.59$ from CustomerId:unknowCustomerId", "meteor@open-sky.fr");

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, never()).save(userArgumentCaptor.capture());
    }

    @Test
    void shouldClearDonatelyResponseCacheAfterNewDonation() {
        //GIVEN
        Charge charge = buildNewCharge("cus_IyFWZ6cmcXsZEY");

        //WHEN
        stripeService.handleNewDonation(charge);

        //THEN
        verify(donatelyController, times(1)).clearCache();
    }

}
