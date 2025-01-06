package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.ServiceProvider;
import com.wnc.banking.repository.AccountRepository;
import com.wnc.banking.repository.CustomerRepository;
import com.wnc.banking.repository.ServiceProviderRepository;
import com.wnc.banking.security.JwtUtil;
import com.wnc.banking.service.CustomerService;
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

@AllArgsConstructor
@RestController
@RequestMapping("/api/protected/information")
public class InformationController {
    private final CustomerService customerService;
    private final AccountRepository accountRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final CustomerRepository customerRepository;

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
