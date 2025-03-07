package com.rocketmotordesign.admin.service;

import com.google.gson.JsonSyntaxException;
import com.rocketmotordesign.admin.StripeException;
import com.rocketmotordesign.admin.controller.DonatelyController;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.services.IMailService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
public class StripeService {

    private static final Logger LOGGER = LoggerFactory.getLogger("stripe");
    public static final String UNKNOW_CUSTOMER = " unknow customer";

    private final String webhookSigningSecret;
    private final String mailAlertReceiver;
    private final UserRepository userRepository;
    private final IMailService mailService;

    private final DonatelyController donatelyController;


    public StripeService(@Value("${stripe.webhook.signing.secret}") String webhookSigningSecret,
                         @Value("${stripe.mail.alert.new.donation}") String mailAlertReceiver,
                         UserRepository userRepository,
                         IMailService mailService, DonatelyController donatelyController) {
        this.webhookSigningSecret = webhookSigningSecret;
        this.mailAlertReceiver = mailAlertReceiver;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.donatelyController = donatelyController;
    }

    public Event retrieveEvent(String sigHeader, String payload) throws StripeException {
        try {
            return Webhook.constructEvent(
                    payload, sigHeader, webhookSigningSecret
            );
        } catch (JsonSyntaxException e) {
            // Invalid payload
            LOGGER.error("Invalid Stripe paylod", e);
            throw new StripeException(e);
        } catch (SignatureVerificationException e) {
            // Invalid signature
            LOGGER.error("Stripe invalid signature", e);
            throw new StripeException(e);
        }

    }

    public StripeObject getStripeObject(Event event) throws StripeException {
        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            return dataObjectDeserializer.getObject().get();
        } else {
            LOGGER.error("Failed to deserialize EventDataObjectDeserializer, probably API version mismatch with Stripe");
            throw new StripeException("Failed to deserialize EventDataObjectDeserializer, probably API version mismatch with Stripe");
        }
    }

    public void handleNewDonation(Charge charge) {
        Optional<User> byStripeCustomerId = userRepository.findByStripeCustomerId(charge.getCustomer());
        byStripeCustomerId
                .ifPresentOrElse(
                        this::processNewDonation,
                        () -> LOGGER.error("Can't update last donation date to customer {} because no user has this customerId", charge.getCustomer())
                );

        try {
            StringBuilder messageBuilder = new StringBuilder("You receive a new donation of ")
                    .append(new BigDecimal(charge.getAmount()).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP))
                    .append("$ from ")
                    .append(byStripeCustomerId.map(User::getUsername).orElse("CustomerId:"+charge.getCustomer()));
            mailService.sendHtmlMessage("METEOR : New donation", messageBuilder.toString(), mailAlertReceiver);
            donatelyController.clearCache();
        } catch (MessagingException e) {
            LOGGER.error("Failed to send mail for new donation");
        }
    }

    private void processNewDonation(User user) {
        user.setLastDonation(now());
        userRepository.save(user);
    }

    private void registerNewDonation(User user) {
        user.setLastDonation(now());
        userRepository.save(user);
    }

    public void registerNewDonator(Customer customer) {
        userRepository.findByEmail(customer.getEmail())
                .ifPresentOrElse(
                        user -> registerStripeCustomer(user, customer),
                        () -> LOGGER.error("Can't mark {} as donator because this mail is not in METEOR", customer.getEmail()));
    }

    private void registerStripeCustomer(User user, Customer customer) {
        if(customer.getId() != null) {
            user.setStripeCustomerId(customer.getId());
            userRepository.save(user);
        } else {
            LOGGER.error("No customer ID for user {}", customer.getId());
        }
    }
}
