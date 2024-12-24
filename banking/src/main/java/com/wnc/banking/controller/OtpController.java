package com.wnc.banking.controller;

import com.wnc.banking.dto.OtpDto;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.service.CustomerService;
import com.wnc.banking.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/otp")
public class OtpController {
    @Autowired
    private OtpService otpService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOtp(@RequestBody OtpDto otpRequest) {
        String accountEmail = otpRequest.getEmail();

        Customer customer = customerService.getCustomerByEmail(accountEmail);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found for the given account number");
        }

        String otp = otpService.generateOTP(accountEmail);
        CompletableFuture<Boolean> emailSendingFuture = otpService.sendOTPByEmail(customer.getEmail(), customer.getName(), customer.getAccount().getAccountNumber(), otp);

        try {
            boolean otpSent = emailSendingFuture.get();

            if (otpSent) {
                return ResponseEntity.ok().body("{\"message\": \"OTP sent successfully\"}");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Failed to send OTP\"}");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Failed to send OTP\"}");
        }
    }
}
