package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.ChangePasswordRequest;
import com.wnc.banking.dto.CustomerDTO;
import com.wnc.banking.dto.OnUpdateDTO;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/protected/customer")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            if (customers != null && !customers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all customers successfully"), customers));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any customers"), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @GetMapping("/{email}")
    ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerByNameOrEmail(@PathVariable String email) {
        String EMAIL_REGEX = "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,}$";
        if (email.matches(EMAIL_REGEX)) {
            try {
                Customer customer = customerService.getCustomerByEmail(email);
                if (customer != null) {
                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put("accountNumber", customer.getAccount().getAccountNumber());
                    responseData.put("balance", customer.getAccount().getBalance());

                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get customer with email: " + email + " successfully"), responseData));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot found customer with email: " + email), null));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Invalid email address"), null));
        }
    }

    @PatchMapping("/update-profile/{email}")
    ResponseEntity<ApiResponse<Void>> updateCustomer(@PathVariable String email, @RequestBody @Validated(OnUpdateDTO.class) CustomerDTO customerDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>(result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        }
        try {
            String message = customerService.updateCustomer(email, customerDto);
            if (!message.contains("Cannot found customer")) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(message), null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of(message), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @PatchMapping("/change-password/{email}")
    ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable String email, @RequestBody @Validated(OnUpdateDTO.class) ChangePasswordRequest changePasswordRequest, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>(result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        }
        try {
            String message = customerService.changePassword(email, changePasswordRequest);
            if (message.contains("Old password is incorrect")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of(message), null));
            } else if (message.contains("Cannot found customer")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of(message), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(message), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @PostMapping()
    ResponseEntity<ApiResponse<Void>> createCustomer(@RequestBody @Validated(OnUpdateDTO.class) CustomerDTO customerDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        }
        try {
            String message = customerService.createCustomer(customerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, List.of(message), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }
}
