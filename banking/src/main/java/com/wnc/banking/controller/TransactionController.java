package com.wnc.banking.controller;

import com.wnc.banking.dto.*;
import com.wnc.banking.entity.EmployeeTransaction;
import com.wnc.banking.entity.Transaction;
import com.wnc.banking.repository.TransactionRepository;
import com.wnc.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/protected/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(@Valid  @RequestBody TransactionDTO request) {
        try {
            Transaction created = transactionService.createTransaction(request);
            return ResponseEntity.status(200).body(new ApiResponse<>(true, List.of("Transaction created!"), created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<EmployeeTransaction>> deposit(@Valid @RequestBody EmployeeTransactionDTO request) {
        try {
            EmployeeTransaction created = transactionService.createEmployeeTransaction(request);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,  List.of("Employee Transaction created!"), created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @GetMapping("/deposit")
    public ResponseEntity<ApiResponse<List<EmployeeTransaction>>> getAllDeposit() {
        try {
            List<EmployeeTransaction> employeeTransactions = transactionService.getAllEmployeeTransaction();
            if (employeeTransactions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any employee transactions"), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all employee transactions successfully"), employeeTransactions));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @GetMapping("/deposit/service-provider/{id}")
    public ResponseEntity<ApiResponse<List<EmployeeTransaction>>> getDepositByServiceProvider(@PathVariable String id) {
        try {
            List<EmployeeTransaction> employeeTransactions = transactionService.getEmployeeTransactionByServiceProvider(id);
            if (employeeTransactions == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Cannot find service provider with id: " + id), null));
            } else if (employeeTransactions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any employee transactions"), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all employee transactions successfully"), employeeTransactions));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @GetMapping("/deposit/account/{id}")
    public ResponseEntity<ApiResponse<List<EmployeeTransaction>>> getDepositByReceiverAccount(@PathVariable String id) {
        try {
            List<EmployeeTransaction> employeeTransactions = transactionService.getEmployeeTransactionByReceiverAccount(id);
            if (employeeTransactions == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Cannot find receiver account with id: " + id), null));
            } else if (employeeTransactions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any employee transactions"), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all employee transactions successfully"), employeeTransactions));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @GetMapping("/transaction/account/{id}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionByAccount(@PathVariable String id) {
        try {
            List<TransactionResponse> transactions = transactionService.getTransactionByAccount(id);
            if (transactions == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Cannot find account with id: " + id), null));
            } else if (transactions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any transactions"), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all transactions successfully"), transactions));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @GetMapping("/dept/account/{id}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getDeptTransactionByAccount(@PathVariable String id) {
        try {
            List<Transaction> transactions = transactionService.getDeptTransactionByAccount(id);
            if (transactions == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Cannot find account with id: " + id), null));
            } else if (transactions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any dept transactions"), null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all dept transactions successfully"), transactions));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<List<AllTransactionResponse>> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        List<AllTransactionResponse> allTransactionResponses = new ArrayList<>();
        for(Transaction transaction : transactions) {
            AllTransactionResponse transactionResponse = new AllTransactionResponse();
            if(transaction.getType().equals("internal")) {
                transactionResponse.setSendBank("DOMLand Bank");
                transactionResponse.setReceiveBank("DOMLand Bank");
                transactionResponse.setAmount(String.valueOf(transaction.getAmount()));
                transactionResponse.setSendingAccount(transaction.getSenderAccount().getAccountNumber());
                transactionResponse.setReceivingAccount(transaction.getReceiverAccount().getAccountNumber());
                transactionResponse.setDate(transaction.getCreatedAt().toString());
                transactionResponse.setAction("Internal Transaction");
            } else if(transaction.getType().equals("external")) {
                transactionResponse.setSendBank("DOMLand Bank");
                transactionResponse.setReceiveBank("Team 3 Bank");
                transactionResponse.setAmount(String.valueOf(transaction.getAmount()));
                transactionResponse.setSendingAccount(transaction.getSenderAccount().getAccountNumber());
                transactionResponse.setReceivingAccount(transaction.getReceiverAccount().getAccountNumber());
                transactionResponse.setDate(transaction.getCreatedAt().toString());
                transactionResponse.setAction("External Transaction");
            } else {
                transactionResponse.setSendBank("DOMLand Bank");
                transactionResponse.setReceiveBank("DOMLand Bank");
                transactionResponse.setAmount(String.valueOf(transaction.getAmount()));
                transactionResponse.setSendingAccount(transaction.getSenderAccount().getAccountNumber());
                transactionResponse.setReceivingAccount(transaction.getReceiverAccount().getAccountNumber());
                transactionResponse.setDate(transaction.getCreatedAt().toString());
                transactionResponse.setAction("Debt Transaction");
            }
            allTransactionResponses.add(transactionResponse);
        }
        return ResponseEntity.status(HttpStatus.OK).body(allTransactionResponses);
    }
}
