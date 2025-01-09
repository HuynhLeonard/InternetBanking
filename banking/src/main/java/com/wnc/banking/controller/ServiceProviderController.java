package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.OnCreateDTO;
import com.wnc.banking.dto.OnUpdateDTO;
import com.wnc.banking.dto.ServiceProviderDTO;
import com.wnc.banking.service.ServiceProviderService;
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

import java.util.List;

@Tag(name = "Service Provider", description = "Endpoints for managing service providers - admin and employee")
@RestController
@RequestMapping("/api/protected/service-provider")
@AllArgsConstructor
@SecurityRequirement(name = "Authorize")
public class ServiceProviderController {
    private final ServiceProviderService serviceProviderService;

    @Operation(
            summary = "Get All Service Provider",
            description = "Receive a list of all service providers from the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get All Service Providers Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Get all employees successfully\",\n" +
                                            "  \"data\": \"List{ServiceProviderDTO{name='string', email='string', password='string', role='string', phoneNumber='string', address='string'}}\"\n" +
                                            "}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Service Providers Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find any employees\",\n" +
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
    @GetMapping()
    public ResponseEntity<ApiResponse<List<ServiceProviderDTO>>> getAllServiceProviders() {
        try {
            List<ServiceProviderDTO> serviceProviders = serviceProviderService.getAllServiceProviders();
            if (serviceProviders != null && !serviceProviders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all employees successfully"), serviceProviders));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any employees"), null));
            }
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
    @PostMapping()
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

    @Operation(
            summary = "Update Service Provider Profile",
            description = "Update service provider profile based on the provided email address"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile Updated Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Update employee successfully\",\n" +
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
                    responseCode = "404",
                    description = "Service Provider Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot found employee with email: serviceprovider@gmail.com\",\n" +
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
            description = "The email address of the service provider whose profile is being updated",
            required = true,
            example = "serviceproviderr@gmail.com"
    )
    @PatchMapping("update-employee/{email}")
    ResponseEntity<ApiResponse<String>> updateEmployee(@PathVariable String email, @RequestBody @Validated(OnUpdateDTO.class) ServiceProviderDTO serviceProviderDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        }
        try {
            String message = serviceProviderService.updateServiceProvider(email, serviceProviderDto);

            if (message.contains("Cannot")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of(message), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(message), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @Operation(
            summary = "Delete Service Provider Profile",
            description = "Delete service provider profile based on the provided email address"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile Deleted Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Delete employee successfully\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Service Provider Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot found employee with email: serviceprovider@gmail.com\",\n" +
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
            description = "The email address of the service provider whose profile is being deleted",
            required = true,
            example = "serviceproviderr@gmail.com"
    )
    @DeleteMapping("delete-employee/{email}")
    ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable String email) {
        try {
            String message = serviceProviderService.deleteServiceProvider(email);
            if (message.contains("Cannot")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of(message), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(message), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }
}
