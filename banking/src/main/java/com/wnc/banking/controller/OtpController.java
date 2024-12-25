package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.OtpDto;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.service.CustomerService;
import com.wnc.banking.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@RequestBody OtpDto otpRequest) {
        try {
            boolean isValid = otpService.verifyOTP(otpRequest.getEmail(), otpRequest.getOtp());
            if (isValid) {
                return ResponseEntity.ok((new ApiResponse<>(true, List.of("OTP verified successfully."), null)));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,List.of( "Invalid or expired OTP."), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }
}
