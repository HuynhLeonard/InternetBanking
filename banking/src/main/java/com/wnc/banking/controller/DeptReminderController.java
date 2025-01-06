package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.DeptReminderDTO;
import com.wnc.banking.entity.DeptReminder;
import com.wnc.banking.service.DeptReminderService;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Dept Reminder", description = "Endpoints for managing dept reminders")
@RestController
@RequestMapping("/api/protected/dept-reminder")
@AllArgsConstructor
@SecurityRequirement(name = "Authorize")
public class DeptReminderController {
    private final DeptReminderService deptReminderService;

    @Operation(
            summary = "Get Dept Reminders By Sender Account Number",
            description = "Receive all dept reminders sent by a specific account number"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Dept Reminders Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": [\"Get all dept reminders successfully\"],\n" +
                                            "  \"data\": [\"List{DeptReminder{id='string', name='string', senderAccountId='string', receiverAccountId='string', amount='long', description='string', status='string', createdAt='time'}}\"]\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Dept Reminder Not Found",
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
                                            name = "No dept reminders exists",
                                            description = "There are no dept reminders base on sender account number provided exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot find any dept reminders\",\n" +
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
            description = "The account number of the sender",
            required = true,
            example = "012345678910"
    )
    @GetMapping("/send/{senderAccountNumber}")
    ResponseEntity<ApiResponse<List<DeptReminder>>> getBySenderAccountNumber(@PathVariable String senderAccountNumber) {
        try {
            List<DeptReminder> deptReminders = deptReminderService.getBySenderAccountNumber(senderAccountNumber);

            if (deptReminders == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find sender account number"), null));
            } else if (deptReminders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any dept reminders"), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all dept reminders successfully"), deptReminders));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @Operation(
            summary = "Get Dept Reminders By Receiver Account Number",
            description = "Receive all dept reminders received by a specific account number"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Dept Reminders Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": [\"Get all dept reminders successfully\"],\n" +
                                            "  \"data\": [\"List{DeptReminder{id='string', name='string', senderAccountId='string', receiverAccountId='string', amount='long', description='string', status='string', createdAt='time'}}\"]\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Dept Reminder Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid receiver account number",
                                            description = "The receiver account number provided not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot find receiver account\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "No dept reminders exists",
                                            description = "There are no dept reminders base on receiver account number provided exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot find any dept reminders\",\n" +
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
            name = "receiverAccountNumber",
            description = "The account number of the receiver",
            required = true,
            example = "012345678910"
    )
    @GetMapping("/receive/{receiverAccountNumber}")
    ResponseEntity<ApiResponse<List<DeptReminder>>> getByReceiverAccountNumber(@PathVariable String receiverAccountNumber) {
        try {
            List<DeptReminder> deptReminders = deptReminderService.getByReceiverAccountNumber(receiverAccountNumber);

            if (deptReminders == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find receiver account"), null));
            } else if (deptReminders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any dept reminders"), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all dept reminders successfully"), deptReminders));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @Operation(
            summary = "Create New Dept Reminder",
            description = "Create a new dept reminder with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Dept Reminder Created Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": [\"Create dept reminder successfully\"],\n" +
                                            "  \"data\": null\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid sender account number",
                                            description = "The sender account number provided must be 10 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Sender account number must be 10 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid receiver account number",
                                            description = "The receiver account number provided must be 10 characters",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Receiver account number must be 10 characters\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
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
                                            name = "Missing amount",
                                            description = "The amount of dept is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Amount is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid amount",
                                            description = "The amount provided must be larger than 0",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Amount must be larger than 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Dept Reminder Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid sender account number",
                                            description = "The sender account number provided not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Cannot find sender account\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid receiver account number",
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
    @PostMapping("/create")
    ResponseEntity<ApiResponse<Void>> createDeptReminder(@RequestBody DeptReminderDTO deptReminderDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>(result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                List<String> message = deptReminderService.createDeptReminder(deptReminderDTO);

                if (message.contains("Cannot")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, message, null));
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, message, null));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        }
    }

    @Operation(
            summary = "Delete Dept Reminder",
            description = "Delete a dept reminder based on the provided ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Dept Reminder Deleted Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": [\"Delete dept reminder successfully\"],\n" +
                                            "  \"data\": null\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Dept Reminder Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": [\"Cannot find dept reminder\"],\n" +
                                            "  \"data\": null\n" +
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
            name = "id",
            description = "The ID of the dept reminder to be deleted",
            required = true,
            example = "1"
    )
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteDeptReminder(@PathVariable Integer id) {
        try {
            String message = deptReminderService.deleteDeptReminder(id);
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
