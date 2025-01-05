package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.ChangePasswordRequest;
import com.wnc.banking.dto.CustomerDTO;
import com.wnc.banking.dto.OnUpdateDTO;
import com.wnc.banking.entity.Customer;
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
                    description = "Update Profile Successfully",
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
                    content = @Content(mediaType = "application/json")
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
