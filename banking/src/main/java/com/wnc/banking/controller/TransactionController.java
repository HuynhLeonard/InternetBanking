package com.wnc.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnc.banking.client.PartnerClient;
import com.wnc.banking.dto.*;
import com.wnc.banking.entity.EmployeeTransaction;
import com.wnc.banking.entity.ExternalTransaction;
import com.wnc.banking.entity.Transaction;
import com.wnc.banking.repository.ExternalTransactionRepository;
import com.wnc.banking.repository.TransactionRepository;
import com.wnc.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/protected/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionRepository transactionRepository;
    private final ExternalTransactionRepository externalTransactionRepository;
    private TransactionService transactionService;
    private final PartnerClient partnerClient;
    private final ObjectMapper objectMapper;

    public ResponseEntity<ApiResponse<?>> createTransaction(@Valid  @RequestBody TransactionDTO request) {
        try {
            if (request.getType().equals("external")) {
                ExternalDepositRequest requestdeposit = new ExternalDepositRequest();
                requestdeposit.setAmount(request.getAmount());
                requestdeposit.setDescription(request.getDescription());
                requestdeposit.setIsSourceFee(false);
                requestdeposit.setSrcAccountNumber(request.getSenderAccountNumber());
                requestdeposit.setDesAccountNumber(request.getReceiverAccountNumber());
                requestdeposit.setSrcBankCode("BANK1");
                QueryPartnerCustomerRequest queryPartnerCustomerRequest = new QueryPartnerCustomerRequest();
                queryPartnerCustomerRequest.setDesAccountNumber(request.getReceiverAccountNumber());
                queryPartnerCustomerRequest.setSrcBankCode("BANK1");
                ApiResponsePartnerTeam3 responsePartnerTeam3 = partnerClient.query(queryPartnerCustomerRequest);
                //requestdeposit.setForeignAccountName(responsePwartnerTeam3.getData().getDesAccountName());
                Bank1DTO dto = objectMapper.convertValue(responsePartnerTeam3.getData(), Bank1DTO.class);
                partnerClient.deposit(requestdeposit, dto.getDesAccountName());
                return ResponseEntity.status(200).body(new ApiResponse<>(true, List.of("External transaction created!"), null));
            }
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
        List<ExternalTransaction> transactions = externalTransactionRepository.findAll();
        List<AllTransactionResponse> res = new ArrayList<>();
        for (ExternalTransaction externalTransaction : transactions) {
            AllTransactionResponse transaction = new AllTransactionResponse();
            transaction.setDate(externalTransaction.getCreatedAt().toString());
            transaction.setAmount(externalTransaction.getAmount().toString());

            if(externalTransaction.getType().equals("in")) {
                transaction.setSendBank("1");
                transaction.setReceiveBank("0");
                transaction.setSendingAccount(externalTransaction.getForeignAccountNumber());
                transaction.setReceivingAccount(externalTransaction.getAccountNumber());
            } else {
                transaction.setSendBank("0");
                transaction.setReceiveBank("1");
                transaction.setReceivingAccount(externalTransaction.getForeignAccountNumber());
                transaction.setSendingAccount(externalTransaction.getAccountNumber());
            }
            res.add(transaction);
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
