package com.wnc.banking.controller;

import com.wnc.banking.dto.*;
import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.ChangePasswordRequest;
import com.wnc.banking.dto.CustomerDTO;
import com.wnc.banking.dto.OnUpdateDTO;
import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.repository.AccountRepository;
import com.wnc.banking.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Customer", description = "Endpoints for customer management")
@RestController
@RequestMapping("/api/protected/customer")
@AllArgsConstructor
@SecurityRequirement(name = "Authorize")
public class CustomerController {
    private final CustomerService customerService;
    private final AccountRepository accountRepository;

    @Operation(
            summary = "Get All Customers",
            description = "Receive a list of all customers from the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get All Customers Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Get all customers successfully\",\n" +
                                            "  \"data\": \"List{Customer{id='string', name='string', email='string', password='string', phoneNumber='string', address='string', createdAt='time', updatedAt='time'}}\"\n" +
                                            "}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Customers Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find any customers\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Internal server error message\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}")))
    })
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


    @Operation(
            summary = "Get Customer By Email",
            description = "Receive customer details based on the provided email address"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Customer Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Get customer with email: customer@gmail.com successfully\",\n" +
                                            "  \"data\": \"Customer{id='string', name='string', email='string', password='string', phoneNumber='string', address='string', createdAt='time', updatedAt='time'}\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid Email Address",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Invalid email address\",\n" +
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
                                            "  \"message\": \"Cannot found customer with email: customer@gmail.com\",\n" +
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
    @Parameter(
            name = "email",
            description = "The email address of the customer to receive",
            required = true,
            example = "customer@gmail.com"
    )
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

    @Operation(
            summary = "Update Customer Profile",
            description = "Update customer profile based on the provided email address"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile Updated Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Update customer successfully\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid name",
                                            description = "The name provided must be between 5 and 20 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Name must be between 5 and 20 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid email address",
                                            description = "The email address provided is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Invalid email address\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid phone number",
                                            description = "The phone number provide must have 10 digits and start with 0",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Phone number must have 10 digits and start with 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid address",
                                            description = "The address provided must be between 10 and 100 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Address must be between 10 and 100 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid password",
                                            description = "The password provided must be between 10 and 100 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Password must be between 8 and 24 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Customer Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot found customer with email: customer@gmail.com\",\n" +
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
    @Parameter(
            name = "email",
            description = "The email address of the customer whose profile is being updated",
            required = true,
            example = "customer@gmail.com"
    )
    @PatchMapping("/update-profile/{email}")
    ResponseEntity<ApiResponse<Void>> updateCustomer(@PathVariable String email, @RequestBody @Validated(OnUpdateDTO.class) CustomerDTO customerDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>(result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
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
    }

    @Operation(
            summary = "Change Customer Password",
            description = "Change the password of a customer based on the provided email address"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Password Changed Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                            value = "{\n" +
                                    "  \"success\": true,\n" +
                                    "  \"message\": \"Change password successfully\",\n" +
                                    "  \"data\": \"null\"\n" +
                                    "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid old password",
                                            description = "The old password provided must be between 8 and 24 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Old password must be between 8 and 24 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid new password",
                                            description = "The new password provided must be between 8 and 24 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"New password must be between 8 and 24 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing old password",
                                            description = "The old password is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Old password is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing new password",
                                            description = "The new password is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"New password is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid old password",
                                            description = "The old password provided is not correct",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Old password is incorrect\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Duplicated password",
                                            description = "The new password is the same with the old one",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"New password must be different from old password\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Customer Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                            value = "{\n" +
                                    "  \"success\": false,\n" +
                                    "  \"message\": \"Cannot found customer with email: customer@gmail.com\",\n" +
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
    @Parameter(
            name = "email",
            description = "The email address of the customer whose password is being changed",
            required = true,
            example = "customer@gmail.com"
    )
    @PatchMapping("/change-password/{email}")
    ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable String email, @RequestBody ChangePasswordRequest changePasswordRequest, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>(result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
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
    }

    @Operation(
            summary = "Create New Customer",
            description = "Create and add a new customer to the system with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Customer Created Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Create customer successfully\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid name",
                                            description = "The name provided must be between 5 and 20 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Name must be between 5 and 20 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid email address",
                                            description = "The email address provided is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Invalid email address\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid phone number",
                                            description = "The phone number provide must have 10 digits and start with 0",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Phone number must have 10 digits and start with 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid address",
                                            description = "The address provided must be between 10 and 100 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Address must be between 10 and 100 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid password",
                                            description = "The password provided must be between 10 and 100 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Password must be between 8 and 24 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing name",
                                            description = "The name is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Name is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing email address",
                                            description = "The email address is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Email is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing password",
                                            description = "The password is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Password is required\",\n" +
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
    @PostMapping()
    ResponseEntity<ApiResponse<Void>> createCustomer(@RequestBody @Validated(OnCreateDTO.class) CustomerDTO customerDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                String message = customerService.createCustomer(customerDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, List.of(message), null));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        }
    }

    @GetMapping("bank/{bankId}/users/{accountNumber}")
    ResponseEntity<?> getCustomerByBankIdAndAccountNumber(@PathVariable String bankId, @PathVariable String accountNumber) {

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
        }

        return ResponseEntity.ok(responseData);
    }
}
