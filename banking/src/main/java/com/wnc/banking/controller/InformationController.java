package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.ServiceProvider;
import com.wnc.banking.repository.AccountRepository;
import com.wnc.banking.repository.CustomerRepository;
import com.wnc.banking.repository.ServiceProviderRepository;
import com.wnc.banking.security.JwtUtil;
import com.wnc.banking.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.wnc.banking.entity.Customer;
import org.springframework.web.bind.annotation.*;
import com.wnc.banking.security.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Information", description = "Endpoints for managing customer information")
@AllArgsConstructor
@RestController
@RequestMapping("/api/protected/information")
@SecurityRequirement(name = "Authorize")
public class InformationController {
    private final CustomerService customerService;
    private final AccountRepository accountRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final CustomerRepository customerRepository;

    @Operation(
            summary = "Get Customer Profile By Account Number",
            description = "Receive customer profile details by a specific account number"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Customer Profile Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": [\"Get customer with account number: 012345678910 successfully\"],\n" +
                                            "  \"data\": [\"accountNumber='string', name='string', id='string', email='string', phoneNumber='string', address='string'\"]\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid Account Number",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Invalid account number\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Customer Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot found customer with account number: 012345678910\",\n" +
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
                                            "  \"message\": [\"Internal server error\"],\n" +
                                            "  \"data\": null\n" +
                                            "}"))
            )
    })
    @Parameter(
            name = "accountNumber",
            description = "The account number of the customer whose profile is being got",
            required = true,
            example = "012345678910"
    )
    @GetMapping("get-info/{accountNumber}")
    ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerByNameOrEmail(@PathVariable String accountNumber) {
        if (accountNumber != null && !accountNumber.isEmpty()) {
            try {
                Customer customer = customerService.getCustomerByAccountNumber(accountNumber);

                if (customer != null) {
                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put("accountNumber", customer.getAccount().getAccountNumber());
                    responseData.put("name", customer.getName());
                    responseData.put("id", customer.getId());
                    responseData.put("email", customer.getEmail());
                    responseData.put("phoneNumber", customer.getPhoneNumber());
                    responseData.put("address", customer.getAddress());

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ApiResponse<>(true, List.of("Get customer with account number: " + accountNumber + " successfully"), responseData));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, List.of("Cannot found customer with account number: " + accountNumber), null));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, List.of("Invalid account number"), null));
        }
    }

    @Operation(
            summary = "Get User Profile By Header",
            description = "Receive user profile details by header information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get User Profile Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\r\n  \"response\": [\r\n    \"accountNumber=\'string\', name=\'string\', id=\'string\', email=\'string\', phoneNumber=\'string\', address=\'string\'\",\r\n    \"OR\",\r\n    \"name=\'string\', id=\'string\', email=\'string\', phoneNumber=\'string\', address=\'string\'\"\r\n  ]\r\n}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid Account Number",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"response\": [\"Invalid Authorization header\"]\n" +
                                            "}"))
            )
    })
    @GetMapping()
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) throws InstantiationException, IllegalAccessException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header");
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = JwtUtil.extractEmail(token);
        String role = JwtUtil.extractRole(token);

        Map<String, Object> responseData = new HashMap<>();
        if(role.equals("customer")) {
            Customer customer = customerService.getCustomerByEmail(email);
            responseData.put("accountNumber", customer.getAccount().getAccountNumber());
            responseData.put("accountId", customer.getAccount().getId());
            responseData.put("name", customer.getName());
            responseData.put("id", customer.getId());
            responseData.put("email", customer.getEmail());
            responseData.put("phoneNumber", customer.getPhoneNumber());
            responseData.put("address", customer.getAddress());
            responseData.put("balance", customer.getAccount().getBalance());
            responseData.put("role", "customer");
        } else {
            ServiceProvider customer = serviceProviderRepository.findByEmail(email);
            responseData.put("name", customer.getName());
            responseData.put("id", customer.getId());
            responseData.put("email", customer.getEmail());
            responseData.put("phoneNumber", customer.getPhoneNumber());
            responseData.put("address", customer.getAddress());
        }
        return ResponseEntity.ok().body(responseData);
    }

    @Operation(
            summary = "Get Account Profile By Account Number",
            description = "Receive account profile details by account number"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Account Profile Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"response\": [\"accountNumber='string', name='string', id='string', email='string', phoneNumber='string', address='string', balance='string', role='string'\"]\n" +
                                            "}"))
            )
    })
    @Parameter(
            name = "accountNumber",
            description = "The account number of the account which profile being received",
            required = true,
            example = "012345678910"
    )
    @GetMapping("account/{accountNumber}")
    public ResponseEntity<?> getAccount(@PathVariable String accountNumber) throws InstantiationException, IllegalAccessException {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        Map<String, Object> responseData = new HashMap<>();
        Customer customer = customerRepository.findByAccount(account);
        responseData.put("accountNumber", accountNumber);
        responseData.put("name", customer.getName());
        responseData.put("id", customer.getId());
        responseData.put("email", customer.getEmail());
        responseData.put("phoneNumber", customer.getPhoneNumber());
        responseData.put("address", customer.getAddress());
        responseData.put("balance", customer.getAccount().getBalance());
        responseData.put("role", "customer");

        return ResponseEntity.ok().body(responseData);
    }
}
