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
}
