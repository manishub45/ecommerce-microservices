package com.payment_service.controller;



import com.payment_service.dto.PaymentRequestDto;
import com.payment_service.dto.PaymentResponseDto;
import com.payment_service.service.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto requestDto) throws StripeException {
        PaymentResponseDto response = paymentService.createPayment(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm/{stripePaymentIntentId}")
    public ResponseEntity<PaymentResponseDto> confirmPayment(@PathVariable String stripePaymentIntentId) throws StripeException {
        PaymentResponseDto response = paymentService.confirmPayment(stripePaymentIntentId);
        return ResponseEntity.ok(response);
    }
}