package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.OtpDto;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.repository.OtpRepository;
import com.wnc.banking.service.CustomerService;
import com.wnc.banking.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Tag(name = "OTP", description = "Endpoints for managing customer OTP")
@RestController
@RequestMapping("/otp")
public class OtpController {
    @Autowired
    private OtpService otpService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OtpRepository otpRepository;

    @Operation(
            summary = "Create New OTP",
            description = "Create new otp for customer with provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "OTP Created Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"OTP sent successfully\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Customer Not Found",
                                            description = "The customer information provided not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"User not found for the given account number\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Existed OTP",
                                            description = "The email address provided is already received OTP",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"OTP already generated for this email\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Internal server error message\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            )
    })
    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOtp(@RequestBody OtpDto otpRequest) {
        String accountEmail = otpRequest.getEmail();

        Customer customer = customerService.getCustomerByEmail(accountEmail);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,List.of( "User not found for the given account number"), null));
        }

        if (otpRepository.findByEmail(accountEmail) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,List.of( "OTP already generated for this email"), null));
        }

        String otp = otpService.generateOTP(accountEmail);
        CompletableFuture<Boolean> emailSendingFuture = otpService.sendOTPByEmail(customer.getEmail(), customer.getName(), customer.getAccount().getAccountNumber(), otp);

        try {
            boolean otpSent = emailSendingFuture.get();

            if (otpSent) {
                return ResponseEntity.ok().body((new ApiResponse<>(true, List.of("OTP sent successfully"), null)));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false,List.of( "Failed to send OTP"), null));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false,List.of( "Failed to send OTP"), null));
        }
    }


    @Operation(
            summary = "Verify OTP",
            description = "Verify OTP with provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OTP Verified Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"OTP verified successfully.\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "OTP Verified Failed",
                    content = @Content(mediaType = "application/json",
                            examples =
                                    @ExampleObject(
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Invalid or expired OTP.\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Internal server error message\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            )
    })
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }
}
