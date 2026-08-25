package com.payment_service.service;

import com.payment_service.dto.PaymentRequestDto;
import com.payment_service.dto.PaymentResponseDto;
import com.payment_service.entity.Payment;
import com.payment_service.enums.PaymentStatus;
import com.payment_service.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) throws StripeException {

        com.stripe.Stripe.apiKey = stripeSecretKey;

        // Step 1: Payment record banao PENDING status ke saath
        Payment payment = new Payment();
        payment.setOrderId(requestDto.getOrderId());
        payment.setAmount(requestDto.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        // Step 2: Amount ko paise mein convert karo (Stripe cents/paise mangta hai)
        long amountInPaise = requestDto.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        // Step 3: Stripe PaymentIntent banao
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInPaise)
                .setCurrency("inr")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Step 4: Payment record ko Stripe PaymentIntent ID ke saath update karo
        payment.setStripePaymentIntentId(paymentIntent.getId());
        paymentRepository.save(payment);

        // Step 5: Response banao
        return new PaymentResponseDto(
                payment.getId(),
                payment.getStatus(),
                paymentIntent.getClientSecret()
        );
    }

    public PaymentResponseDto confirmPayment(String stripePaymentIntentId) throws StripeException {

        com.stripe.Stripe.apiKey = stripeSecretKey;

        // Step 1: Stripe se current status mangao
        PaymentIntent paymentIntent = PaymentIntent.retrieve(stripePaymentIntentId);
        String stripeStatus = paymentIntent.getStatus();

        // Step 2: Apna Payment record dhoondo
        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId);

        // Step 3: Stripe ka status apne PaymentStatus enum mein convert karo
        if (stripeStatus.equals("succeeded")) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else if (stripeStatus.equals("canceled")) {
            payment.setStatus(PaymentStatus.FAILED);
        } else {
            payment.setStatus(PaymentStatus.PENDING);
        }

        // Step 4: Save karo
        payment = paymentRepository.save(payment);

        // Step 5: Response bhejo
        return new PaymentResponseDto(
                payment.getId(),
                payment.getStatus(),
                null
        );
    }
}
