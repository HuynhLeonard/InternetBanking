package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.ReceiverDTO;
import com.wnc.banking.entity.Receiver;
import com.wnc.banking.service.ReceiverService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/protected/receiver")
@AllArgsConstructor
public class ReceiverController {
    private final ReceiverService receiverService;

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

    @PostMapping
    ResponseEntity<ApiResponse<Void>> createReceiver(@RequestBody ReceiverDTO receiverDTO) {
        try {
            String message = receiverService.createReceiver(receiverDTO);
            if (message.contains("Cannot")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of(message), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(message), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @PatchMapping
    ResponseEntity<ApiResponse<Void>> updateReceiver(@RequestBody ReceiverDTO receiverDTO) {
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
