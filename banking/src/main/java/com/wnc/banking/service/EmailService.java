package com.wnc.banking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public CompletableFuture<Void> sendEmail(String to, String subject, String text) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);

            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);

            future.complete(null);
        } catch (MessagingException e) {
            e.printStackTrace();
            future.completeExceptionally(e);
        }

        return future;
    }

    public String getOtpLoginEmailTemplate(String name, String accountNumber, String otp) {
        return "<div>"
                + "<h3>Hi, " + name + "</h3>"
                + "<p>Account Number: " + accountNumber + "</p>"
                + "<p>Your OTP is:</p>"
                + "<h2>" + otp + "</h2>"
                + "<p>Regards,</p>"
                + "<p>DomLandBank Team</p>"
                + "</div>";
    }
}
