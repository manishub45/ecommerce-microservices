package com.payment_service.service;

import com.payment_service.client.AuthServiceClient;
import com.payment_service.dto.PaymentRequestDto;
import com.payment_service.dto.PaymentResponseDto;
import com.payment_service.dto.PaymentStatusEvent;
import com.payment_service.dto.UserDto;
import com.payment_service.entity.Payment;
import com.payment_service.enums.PaymentStatus;
import com.payment_service.kafka.PaymentEventProducer;
import com.payment_service.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuthServiceClient authServiceClient;
    private final PaymentEventProducer paymentEventProducer;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PaymentService(PaymentRepository paymentRepository, AuthServiceClient authServiceClient,
                          PaymentEventProducer paymentEventProducer) {
        this.paymentRepository = paymentRepository;
        this.authServiceClient = authServiceClient;
        this.paymentEventProducer = paymentEventProducer;
    }

    public PaymentResponseDto createPayment(PaymentRequestDto requestDto, String idempotencyKey) throws StripeException {

        com.stripe.Stripe.apiKey = stripeSecretKey;

        Payment payment = new Payment();
        payment.setOrderId(requestDto.getOrderId());
        payment.setUserId(requestDto.getUserId());
        payment.setAmount(requestDto.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        long amountInPaise = requestDto.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInPaise)
                .setCurrency("inr")
                .putMetadata("orderId", String.valueOf(requestDto.getOrderId()))
                .build();

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params, requestOptions);

        payment.setStripePaymentIntentId(paymentIntent.getId());
        paymentRepository.save(payment);

        return new PaymentResponseDto(
                payment.getId(),
                payment.getStatus(),
                paymentIntent.getClientSecret()
        );
    }

    public PaymentResponseDto confirmPayment(String stripePaymentIntentId) throws StripeException {

        com.stripe.Stripe.apiKey = stripeSecretKey;

        PaymentIntent paymentIntent = PaymentIntent.retrieve(stripePaymentIntentId);
        String stripeStatus = paymentIntent.getStatus();

        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId);

        if (stripeStatus.equals("succeeded")) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else if (stripeStatus.equals("canceled")) {
            payment.setStatus(PaymentStatus.FAILED);
        } else {
            payment.setStatus(PaymentStatus.PENDING);
        }

        payment = paymentRepository.save(payment);

        UserDto user = authServiceClient.getUserById(payment.getUserId());

        PaymentStatusEvent event = new PaymentStatusEvent(
                payment.getOrderId(),
                payment.getStatus().name(),
                user.getEmail()
        );
        paymentEventProducer.sendPaymentStatusEvent(event);

        return new PaymentResponseDto(
                payment.getId(),
                payment.getStatus(),
                null
        );
    }

    public PaymentResponseDto refundPayment(Long orderId) throws StripeException {

        com.stripe.Stripe.apiKey = stripeSecretKey;

        Payment payment = paymentRepository.findByOrderId(orderId);

        if (payment == null) {
            throw new IllegalStateException("No payment found for orderId: " + orderId);
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot refund. Payment is not in SUCCESS state. Current status: " + payment.getStatus());
        }

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(payment.getStripePaymentIntentId())
                .build();

        Refund refund = Refund.create(params);

        payment.setRefundId(refund.getId());
        paymentRepository.save(payment);

        return new PaymentResponseDto(
                payment.getId(),
                payment.getStatus(),
                null
        );
    }

    public void confirmRefund(String stripePaymentIntentId) {

        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId);

        if (payment == null) {
            return;
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);

        UserDto user = authServiceClient.getUserById(payment.getUserId());

        PaymentStatusEvent event = new PaymentStatusEvent(
                payment.getOrderId(),
                payment.getStatus().name(),
                user.getEmail()
        );
        paymentEventProducer.sendPaymentStatusEvent(event);
    }
}