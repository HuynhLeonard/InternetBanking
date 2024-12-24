package com.wnc.banking.service;

import com.wnc.banking.entity.Otp;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpRepository otpRepository;

    public String generateOTP(String email) {
        Random random = new Random();
        int otpValue = 100_000 + random.nextInt(900_000);
        String otp = String.valueOf(otpValue);

        Otp otpInfo = new Otp();
        otpInfo.setEmail(email);
        otpInfo.setOtp(otp);
        otpInfo.setCreatedAt(LocalDateTime.now());
        otpInfo.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpInfo);
        return otp;
    }

    @Async
    public CompletableFuture<Boolean> sendOTPByEmail(String email, String name, String accountNumber, String otp) {
        String subject = "OTP Verification";
        String emailText = emailService.getOtpLoginEmailTemplate(name, "xxxx" + accountNumber.substring(4), otp);

        CompletableFuture<Void> emailSendingFuture = emailService.sendEmail(email, subject, emailText);

        return emailSendingFuture.thenApplyAsync(result -> true)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return false;
                });
    }
}
