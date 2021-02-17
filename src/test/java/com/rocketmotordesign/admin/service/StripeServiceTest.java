package com.rocketmotordesign.admin.service;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.services.MailService;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
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
        given(customer.getEmail()).willReturn(user.getEmail());
        given(userRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(user));

        //WHEN
        stripeService.registerNewDonator(customer);

        //THEN
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1))
                .save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue().isDonator()).isTrue();
    }

    @Test
    void shoulSendMailOnNewDonationFromPaymentIntent() throws MessagingException {
        //GIVEN
        PaymentIntent paymentIntent = mock(PaymentIntent.class);
        Customer customer = mock(Customer.class);

        given(customer.getEmail()).willReturn("customer1@test.tt");
        given(paymentIntent.getAmount()).willReturn(2059L);
        given(paymentIntent.getCustomerObject()).willReturn(customer);

        //WHEN
        stripeService.handleNewDonation(paymentIntent);

        //THEN
        verify(mailService, times(1))
                .sendHtmlMessage("METEOR : New donation", "You receive a new donation of 20.59$ from customer1@test.tt", "meteor@open-sky.fr");
    }

    @Test
    void shoulSendMailOnNewDonationFromCharge() throws MessagingException {
        //GIVEN
        Charge charge = mock(Charge.class);
        Customer customer = mock(Customer.class);

        given(customer.getEmail()).willReturn("customer1@test.tt");
        given(charge.getAmount()).willReturn(2059L);
        given(charge.getCustomerObject()).willReturn(customer);

        //WHEN
        stripeService.handleNewDonation(charge);

        //THEN
        verify(mailService, times(1))
                .sendHtmlMessage("METEOR : New donation", "You receive a new donation of 20.59$ from customer1@test.tt", "meteor@open-sky.fr");
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
        Charge charge = mock(Charge.class);
        Customer customer = mock(Customer.class);
        LocalDateTime startOfTest = now();

        given(customer.getEmail()).willReturn("customer1@test.tt");
        given(charge.getAmount()).willReturn(2059L);
        given(charge.getCustomerObject()).willReturn(customer);
        given(userRepository.findByEmail("customer1@test.tt"))
                .willReturn(Optional.of(new User("customer1@test.tt", "pwd")));

        //WHEN
        stripeService.handleNewDonation(charge);

        //THEN
        verify(mailService, times(1))
                .sendHtmlMessage("METEOR : New donation", "You receive a new donation of 20.59$ from customer1@test.tt", "meteor@open-sky.fr");

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue().getLastDonation()).isAfter(startOfTest);

        assertThat(userArgumentCaptor.getValue().isActiveDonator(Duration.ofSeconds(10), now())).isTrue();
    }

    @Test
    void shoulAddLastDonationDateOnNewDonationFromPaymentIntent() throws MessagingException {
        //GIVEN
        PaymentIntent paymentIntent = mock(PaymentIntent.class);
        Customer customer = mock(Customer.class);
        LocalDateTime startOfTest = now();

        given(customer.getEmail()).willReturn("customer1@test.tt");
        given(paymentIntent.getAmount()).willReturn(2059L);
        given(paymentIntent.getCustomerObject()).willReturn(customer);
        given(userRepository.findByEmail("customer1@test.tt"))
                .willReturn(Optional.of(new User("customer1@test.tt", "pwd")));

        //WHEN
        stripeService.handleNewDonation(paymentIntent);

        //THEN
        verify(mailService, times(1))
                .sendHtmlMessage("METEOR : New donation", "You receive a new donation of 20.59$ from customer1@test.tt", "meteor@open-sky.fr");

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue().getLastDonation()).isAfter(startOfTest);
        assertThat(userArgumentCaptor.getValue().isActiveDonator(Duration.ofSeconds(10), now())).isTrue();
    }

    @Test
    void shoulNotAddLastDonationDateOnNewDonationFromChargeWhenUserNotFound() throws MessagingException {
        //GIVEN
        Charge charge = mock(Charge.class);
        Customer customer = mock(Customer.class);
        LocalDateTime startOfTest = now();

        given(customer.getEmail()).willReturn("customer1@test.tt");
        given(charge.getAmount()).willReturn(2059L);
        given(charge.getCustomerObject()).willReturn(customer);
        given(userRepository.findByEmail("customer1@test.tt"))
                .willReturn(empty());

        //WHEN
        stripeService.handleNewDonation(charge);

        //THEN
        verify(mailService, times(1))
                .sendHtmlMessage("METEOR : New donation", "You receive a new donation of 20.59$ from customer1@test.tt", "meteor@open-sky.fr");

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, never()).save(userArgumentCaptor.capture());
    }

    @Test
    void shoulNotAddLastDonationDateOnNewDonationFromPaymentIntentWhenUserNotFound() throws MessagingException {
        //GIVEN
        PaymentIntent paymentIntent = mock(PaymentIntent.class);
        Customer customer = mock(Customer.class);
        LocalDateTime startOfTest = now();

        given(customer.getEmail()).willReturn("customer1@test.tt");
        given(paymentIntent.getAmount()).willReturn(2059L);
        given(paymentIntent.getCustomerObject()).willReturn(customer);
        given(userRepository.findByEmail("customer1@test.tt"))
                .willReturn(empty());

        //WHEN
        stripeService.handleNewDonation(paymentIntent);

        //THEN
        verify(mailService, times(1))
                .sendHtmlMessage("METEOR : New donation", "You receive a new donation of 20.59$ from customer1@test.tt", "meteor@open-sky.fr");

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, never()).save(userArgumentCaptor.capture());
    }
}