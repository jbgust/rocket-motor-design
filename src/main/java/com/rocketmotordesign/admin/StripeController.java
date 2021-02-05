package com.rocketmotordesign.admin;

import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("stripe")
public class StripeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeController.class);
    private final String webhookSigningSecret;

    public StripeController(@Value("${stripe.webhook.signing.secret}") String webhookSigningSecret) {
        this.webhookSigningSecret = webhookSigningSecret;
    }

    @PostMapping(value = "events")
    public ResponseEntity postEventsWebhook(@RequestBody String payload,  HttpServletRequest request, HttpServletResponse response) {
        String sigHeader = request.getHeader("Stripe-Signature");
        Event event = null;

        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, webhookSigningSecret
            );
        } catch (JsonSyntaxException e) {
            // Invalid payload
            LOGGER.error("Invalid Stripe paylod", e);
            return ResponseEntity.badRequest().build();
        } catch (SignatureVerificationException e) {
            // Invalid signature
            LOGGER.error("Stripe invalid signature", e);
            return ResponseEntity.badRequest().build();
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            LOGGER.error("Failed to deserialize EventDataObjectDeserializer, probably API version mismatch with Stripe");
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                LOGGER.info("TOTO : {}", paymentIntent.toJson());
                // Then define and call a method to handle the successful payment intent.
                // handlePaymentIntentSucceeded(paymentIntent);
                break;
            case "customer.created":
                Customer customer = (Customer) stripeObject;
                LOGGER.info("TITI: {}", customer.toJson());
                // Then define and call a method to handle the successful attachment of a PaymentMethod.
                // handlePaymentMethodAttached(paymentMethod);
                break;
            // ... handle other event types
            default:
                LOGGER.trace("STRIPE : unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok().build();
    }
}
