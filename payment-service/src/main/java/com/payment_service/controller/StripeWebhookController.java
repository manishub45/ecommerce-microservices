package com.payment_service.controller;

import com.payment_service.service.PaymentService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public StripeWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/v1/payment/webhook/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        log.info("Stripe webhook received: type={}", event.getType());

        if ("payment_intent.succeeded".equals(event.getType())
                || "payment_intent.payment_failed".equals(event.getType())) {

            try {
                // deserializeUnsafe() can throw a checked exception if deserialization fails
                StripeObject stripeObject = event.getDataObjectDeserializer().deserializeUnsafe();

                if (stripeObject instanceof PaymentIntent paymentIntent) {
                    paymentService.confirmPayment(paymentIntent.getId());
                }
            } catch (EventDataObjectDeserializationException e) {
                log.error("Failed to deserialize payment_intent event", e);
            } catch (Exception e) {
                log.error("Failed to process webhook for payment_intent event", e);
            }

        } else if ("charge.refunded".equals(event.getType())) {

            try {
                StripeObject stripeObject = event.getDataObjectDeserializer().deserializeUnsafe();
                log.info("Deserialized charge.refunded object: {}", stripeObject);

                if (stripeObject instanceof Charge charge) {
                    paymentService.confirmRefund(charge.getPaymentIntent());
                } else {
                    log.warn("charge.refunded event could not be deserialized to Charge object, raw type: {}",
                            stripeObject == null ? "null" : stripeObject.getClass().getName());
                }
            } catch (EventDataObjectDeserializationException e) {
                log.error("Failed to deserialize charge.refunded event", e);
            } catch (Exception e) {
                log.error("Failed to process refund webhook", e);
            }

        } else {
            log.info("Ignoring unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("received");
    }
}