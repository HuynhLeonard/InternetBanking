package com.wnc.banking.controller;

import com.wnc.banking.dto.*;
import com.wnc.banking.service.AuthService;
import com.wnc.banking.service.CustomerService;
import com.wnc.banking.service.ServiceProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CustomerService customerService;
    private final ServiceProviderService serviceProviderService;

    @Operation(
            summary = "Login To The Application",
            description = "Authenticate the user and generate access and refresh tokens if the credentials are valid"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                            value = "{\n" +
                                    "  \"success\": true,\n" +
                                    "  \"message\": \"Successfully logged in\",\n" +
                                    "  \"data\": \"AuthResponse{accessToken='string', refreshToken='string', message='string'}\"\n" +
                                    "}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "User not found",
                                            description = "The email address provided not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot found user with email: user@gmail.com\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid password",
                                            description = "The password provided is not correct",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Invalid password\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing email",
                                            description = "Email address is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Email is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing password",
                                            description = "Password is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Password is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid email address",
                                            description = "The email address provided is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Invalid email address\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                            })),
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
    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                AuthResponse response = authService.login(request.getEmail(), request.getPassword());
                if (response.getAccessToken() == null && response.getRefreshToken() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of(response.getMessage()), null));
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(response.getMessage()), response));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        }
    }

    @Operation(
            summary = "Refresh Access Token",
            description = "Refresh the access token using a valid refresh token"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Access Token Refreshed Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Successfully refresh access token\",\n" +
                                            "  \"data\": \"AuthResponse{accessToken='string', refreshToken='string', message='string'}\"\n" +
                                            "}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid refresh token",
                                            description = "The refresh token provided is not correct",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Invalid refresh token\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Expired refresh token",
                                            description = "The refresh token provided expired",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Refresh token expired\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "User not found",
                                            description = "Extracted data from refresh token is not valid",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot found user with id: userID\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing refresh token",
                                            description = "Refresh token is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Refresh token is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                    })),
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
    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<AuthResponse>> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                AuthResponse response = authService.refreshAccessToken(request.getRefreshToken());
                if (response.getAccessToken() == null && response.getRefreshToken() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of(response.getMessage()), null));
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(response.getMessage()), response));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        }
    }

    @Operation(
            summary = "Register New Account To The Application",
            description = "Create and add a new customer to the system with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Register Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Create customer successfully\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))),
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
                                            "}")))
    })
    @PostMapping("/register")
    ResponseEntity<ApiResponse<Void>> createCustomer(@RequestBody @Validated(OnCreateDTO.class) CustomerDTO customerDto, BindingResult result) {
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

    @Operation(
            summary = "Create New Service Provider",
            description = "Create and add a new service provider to the system with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Service Provider Created Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Create employee successfully\",\n" +
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
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing role",
                                            description = "The role is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Role is required\",\n" +
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
    @PostMapping("/registerEmployee")
    ResponseEntity<ApiResponse<String>> createEmployee(@RequestBody @Validated(OnCreateDTO.class) ServiceProviderDTO serviceProviderDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        }
        try {
            String message = serviceProviderService.createServiceProvider(serviceProviderDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, List.of(message), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }
}
