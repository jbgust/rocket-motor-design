package com.rocketmotordesign.admin;

import com.rocketmotordesign.admin.service.StripeService;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
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

            // Handle the event
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                    LOGGER.info("New donation : {}$", paymentIntent.getAmount());
                    stripeService.handleNewDonation(paymentIntent);
                    break;
                case "customer.created":
                    Customer customer = (Customer) stripeObject;
                    LOGGER.info("New donator  id({})", customer.getId());
                    stripeService.registerNewDonator(customer);
                    break;
                // ... handle other event types
                default:
                    LOGGER.trace("STRIPE : unhandled event type: " + event.getType());
            }
            return ResponseEntity.ok().build();
        } catch (StripeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
