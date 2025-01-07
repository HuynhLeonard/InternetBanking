package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.OnUpdateDTO;
import com.wnc.banking.dto.ReceiverDTO;
import com.wnc.banking.entity.Receiver;
import com.wnc.banking.service.ReceiverService;
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
import java.util.List;

@Tag(name = "Receiver", description = "Endpoints for managing receivers list")
@RestController
@RequestMapping("/api/protected/receiver")
@AllArgsConstructor
@SecurityRequirement(name = "Authorize")
public class ReceiverController {
    private final ReceiverService receiverService;

    @Operation(
            summary = "Get Receivers By Account Number",
            description = "Get receivers list from provided sender account number"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Receivers Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": [\"Get all receivers successfully\"],\n" +
                                            "  \"data\": [\"List{Receiver{id='int', senderAccountId='string', receiverAccountId='string', nickName='string', createdAt='time', updatedAt='time'}}\"]\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Receivers Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid sender account number",
                                            description = "The sender account number provided not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot find sender account number\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "No receiver exists",
                                            description = "There are no receiver base on sender account number provided exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot find any receivers\",\n" +
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
                                            "  \"message\": [\"Internal server error\"],\n" +
                                            "  \"data\": null\n" +
                                            "}"))
            )
    })
    @Parameter(
            name = "senderAccountNumber",
            description = "The account number of the sender whose receivers list is being got",
            required = true,
            example = "012345678910"
    )
    @GetMapping("/{senderAccountNumber}")
    ResponseEntity<ApiResponse<List<Receiver>>> getReceiversByAccountNumber(@PathVariable String senderAccountNumber) {
        try {
            List<Receiver> receivers = receiverService.getReceiversByAccountNumber(senderAccountNumber);

            if (receivers == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find sender account number"), null));
            } else if (receivers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any receivers"), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all receivers successfully"), receivers));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @Operation(
            summary = "Create New Receiver",
            description = "Create and add a new receiver to receivers list for sender account with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Receiver Created Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": [\"Create receiver successfully\"],\n" +
                                            "  \"data\": [\"null\"]\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Missing sender account number",
                                            description = "The sender account number is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Sender account number is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing receiver account number",
                                            description = "The receiver account number is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Receiver account number is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid sender account number",
                                            description = "The sender account number provided is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Sender account number must have 12 digits and start with 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid receiver account number",
                                            description = "The receiver account number provided is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Receiver account number must have 12 digits and start with 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Receivers Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Sender account number not found",
                                            description = "The sender account number provided not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot find sender account\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Receiver account number not found",
                                            description = "The receiver account number provided not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot find receiver account\",\n" +
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
                                            "  \"message\": [\"Internal server error\"],\n" +
                                            "  \"data\": null\n" +
                                            "}"))
            )
    })
    @PostMapping
    ResponseEntity<ApiResponse<Void>> createReceiver(@RequestBody ReceiverDTO receiverDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>(result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                List<String> message = receiverService.createReceiver(receiverDTO);
                if (message.contains("Cannot")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, message, null));
                } else {
                    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, message, null));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        }
    }

    @Operation(
            summary = "Update Receiver Information",
            description = "Update receiver information with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Receiver Updated Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": [\"Update receiver successfully\"],\n" +
                                            "  \"data\": [\"null\"]\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Missing sender account number",
                                            description = "The sender account number is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Sender account number is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing receiver account number",
                                            description = "The receiver account number is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Receiver account number is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid sender account number",
                                            description = "The sender account number provided is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Sender account number must have 12 digits and start with 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid receiver account number",
                                            description = "The receiver account number provided is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Receiver account number must have 12 digits and start with 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Receivers Not Found",
                    content = @Content(mediaType = "application/json",
                            examples =
                                    @ExampleObject(
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot find sender account and receiver account\",\n" +
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
    @PatchMapping
    ResponseEntity<ApiResponse<Void>> updateReceiver(@RequestBody ReceiverDTO receiverDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>(result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                String message = receiverService.updateReceiver(receiverDTO);
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

    @Operation(
            summary = "Delete Receiver Information",
            description = "Delete receiver information with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Receiver Deleted Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": [\"Delete receiver successfully\"],\n" +
                                            "  \"data\": [\"null\"]\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Missing sender account number",
                                            description = "The sender account number is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Sender account number is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing receiver account number",
                                            description = "The receiver account number is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Receiver account number is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid sender account number",
                                            description = "The sender account number provided is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Sender account number must have 12 digits and start with 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid receiver account number",
                                            description = "The receiver account number provided is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Receiver account number must have 12 digits and start with 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Receivers Not Found",
                    content = @Content(mediaType = "application/json",
                            examples =
                            @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find sender account and receiver account\",\n" +
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
    @DeleteMapping
    ResponseEntity<ApiResponse<Void>> deleteReceiver(@RequestBody ReceiverDTO receiverDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>(result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                String message = receiverService.deleteReceiver(receiverDTO);
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
}
