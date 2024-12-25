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
        String emailTemplate = "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">"
                + "<div style=\"margin:50px auto;width:70%;padding:20px 0\">"
                + "<div style=\"border-bottom:1px solid #eee\">"
                + "<a href=\"https://domlandbank.netlify.app/\" style=\"font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600\">DomLandBank</a>"
                + "</div>"
                + "<p style=\"font-size:1.1em\">Hi, " + name + "</p>"
                + "<p style=\"font-size:0.9em;\">Account Number: " + accountNumber + "</p>"
                + "<p>Thank you for choosing DomLandBank. Use the following OTP to complete your Log In procedures. OTP is valid for 5 minutes</p>"
                + "<h2 style=\"background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" + otp + "</h2>"
                + "<p style=\"font-size:0.9em;\">Regards,<br />DomLandBank</p>"
                + "<hr style=\"border:none;border-top:1px solid #eee\" />"
                + "<p>Group 1 Inc</p>"
                + "<p>Anh_Huu_Loc_Phat_Tuan</p>"
                + "<p>Khoa hoc Tu nhien - HCMUS</p>"
                + "</div>"
                + "</div>";

        return emailTemplate;
    }
}
