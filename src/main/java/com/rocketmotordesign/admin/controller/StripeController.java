package com.rocketmotordesign.admin.controller;

import com.rocketmotordesign.admin.StripeException;
import com.rocketmotordesign.admin.service.StripeService;
import com.stripe.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("stripe")
public class StripeController {

    private static final Logger LOGGER = LoggerFactory.getLogger("stripe");
    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping(value = "events")
    public ResponseEntity postEventsWebhook(@RequestBody String payload,  HttpServletRequest request) {
        Event event = null;

        try {
            event = stripeService.retrieveEvent(request.getHeader("Stripe-Signature"), payload);
            StripeObject stripeObject = stripeService.getStripeObject(event);

            LOGGER.info("Event type : {}", event.getType());
            // Handle the event
            switch (event.getType()) {
                case "customer.created":
                    Customer customer = (Customer) stripeObject;
                    LOGGER.info("New donator  id({})", customer.getId());
                    stripeService.registerNewDonator(customer);
                    break;
                case "charge.succeeded":
                    Charge charge = (Charge) stripeObject;
                    LOGGER.info("New charge : {}$", charge.getAmount()/100);
                    stripeService.handleNewDonation(charge);
                    break;
                // ... handle other event types
                default:
                    LOGGER.warn("STRIPE : unhandled event type: " + event.getType());
            }
            return ResponseEntity.ok().build();
        } catch (StripeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
