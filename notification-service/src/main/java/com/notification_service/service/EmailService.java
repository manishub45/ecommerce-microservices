package com.notification_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmationEmail(String toEmail, Long orderId, java.math.BigDecimal totalAmount) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Order Confirmation - Order #" + orderId);
        message.setText("Hi,\n\nYour order #" + orderId + " has been placed successfully.\n"
                + "Total Amount: Rs. " + totalAmount + "\n\nThank you for shopping with us!");

        mailSender.send(message);
    }

    public void sendPaymentStatusEmail(String toEmail, Long orderId, String status) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);

        if (status.equals("SUCCESS")) {
            message.setSubject("Payment Successful - Order #" + orderId);
            message.setText("Hi,\n\nYour payment for order #" + orderId + " was successful.\n\nThank you!");
        } else {
            message.setSubject("Payment Failed - Order #" + orderId);
            message.setText("Hi,\n\nYour payment for order #" + orderId + " has failed. Please try again.\n\nRegards.");
        }

        mailSender.send(message);
    }
}
