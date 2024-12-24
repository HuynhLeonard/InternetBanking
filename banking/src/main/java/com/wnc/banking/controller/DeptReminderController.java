package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.DeptReminderDTO;
import com.wnc.banking.entity.DeptReminder;
import com.wnc.banking.service.DeptReminderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/protected/dept-reminder")
@AllArgsConstructor
public class DeptReminderController {
    private final DeptReminderService deptReminderService;

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

    @GetMapping("/receive/{receiverAccountNumber}")
    ResponseEntity<ApiResponse<List<DeptReminder>>> getByReceiverAccountNumber(@PathVariable String receiverAccountNumber) {
        try {
            List<DeptReminder> deptReminders = deptReminderService.getByReceiverAccountNumber(receiverAccountNumber);

            if (deptReminders == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find receiver account number"), null));
            } else if (deptReminders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any dept reminders"), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all dept reminders successfully"), deptReminders));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @PostMapping("/create")
    ResponseEntity<ApiResponse<Void>> createDeptReminder(@RequestBody DeptReminderDTO deptReminderDTO) {
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
