package com.wnc.banking.controller;

import com.wnc.banking.client.PartnerClient;
import com.wnc.banking.dto.*;
import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.repository.AccountRepository;
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
    private final AccountRepository accountRepository;
    private final PartnerClient partnerClient;

    @GetMapping
    ResponseEntity<?> getAllCustomers() {
//        try {
//            List<Customer> customers = customerService.getAllCustomers();
//            if (customers != null && !customers.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all customers successfully"), customers));
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any customers"), null));
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
//        }
        List<Customer> customers = customerService.getAllCustomers();
        List<Map<String,Object>> dataResponse = new ArrayList<>();
        for (Customer customer : customers) {
            Map<String,Object> data = new HashMap<>();
            data.put("accountNumber", customer.getAccount().getAccountNumber());
            data.put("name", customer.getName());
            data.put("id", customer.getId());
            data.put("email", customer.getEmail());
            data.put("phoneNumber", customer.getPhoneNumber());
            data.put("address", customer.getAddress());
            data.put("balance", customer.getAccount().getBalance());
            data.put("accounntId", customer.getAccount().getId());
            data.put("role", "customer");
            dataResponse.add(data);
        }
        return ResponseEntity.status(HttpStatus.OK).body(dataResponse);
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

    @GetMapping("bank/{bankId}/users/{accountNumber}")
    ResponseEntity<?> getCustomerByBankIdAndAccountNumber(@PathVariable String bankId, @PathVariable String accountNumber) throws Exception{

        Map<String, Object> responseData = new HashMap<>();
        if(bankId.equals("0")) {
            Customer customer = customerService.getCustomerByAccountNumber(accountNumber);
            Account account = accountRepository.findByAccountNumber(accountNumber);

            responseData.put("accountNumber", accountNumber);
            responseData.put("name", customer.getName());
            responseData.put("id", customer.getId());
            responseData.put("email", customer.getEmail());
            responseData.put("phoneNumber", customer.getPhoneNumber());
            responseData.put("address", customer.getAddress());
            responseData.put("balance", customer.getAccount().getBalance());
            responseData.put("role", "customer");
            responseData.put("bankId", bankId);
        } else {
            QueryPartnerCustomerRequest queryPartnerCustomerRequest = new QueryPartnerCustomerRequest();
            queryPartnerCustomerRequest.setDesAccountNumber(accountNumber);
            queryPartnerCustomerRequest.setSrcBankCode("BANK1");
            ApiResponsePartnerTeam3<Bank1DTO> responsePartnerTeam3 = partnerClient.query(queryPartnerCustomerRequest);
            responseData.put("name", responsePartnerTeam3.getData().getDesAccountName());

        }
        else if (bankId.equals("1"))
        // TODO: bankId = 1
        {
            Customer customer = customerService.getCustomerByAccountNumber(accountNumber);
            Account account = accountRepository.findByAccountNumber(accountNumber);
            responseData.put("accountNumber", null);
            responseData.put("name", customer.getName());
            responseData.put("id", null);
            responseData.put("email", null);
            responseData.put("phoneNumber", null);
            responseData.put("address", null);
            responseData.put("balance", null);
            responseData.put("role", "customer");
            responseData.put("bankId", null);
        }

        return ResponseEntity.ok(responseData);
    }
}
