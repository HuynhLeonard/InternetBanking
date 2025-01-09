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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Transaction", description = "Endpoints for managing customer transaction")
@RestController
@RequestMapping("/api/protected/transactions")
@AllArgsConstructor
@SecurityRequirement(name = "Authorize")
public class TransactionController {
    private final TransactionRepository transactionRepository;
    private final ExternalTransactionRepository externalTransactionRepository;
    private TransactionService transactionService;
    private final PartnerClient partnerClient;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "Create New Transaction",
            description = "Create and add a new transaction to the system with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Transaction Created Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Transaction created!\",\n" +
                                            "  \"data\": \"Transaction{amount='int', description='string', type='string', createdAt='time'}\"\n" +
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
                                            name = "Missing amount",
                                            description = "The amount of transaction is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Amount is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing type",
                                            description = "The type of transaction is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Type of transaction is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid amount",
                                            description = "The amount of transaction is lower or equal than 0",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Amount property must be larger than 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid type",
                                            description = "The type of transaction is incorrect format",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Type must be one of the following: internal, external, dept\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
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
    @PostMapping
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

    @Operation(
            summary = "Create New Employee Transaction - Deposit",
            description = "Create and add a new employee transaction (deposit for specific account) to the system with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Transaction Created Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Employee Transaction created!\",\n" +
                                            "  \"data\": \"EmployeeTransaction{id='string', amount='int', createdAt='time'}\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Missing service provider ID",
                                            description = "The service provider ID is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Service Provider ID property cannot be empty\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing receiver account number",
                                            description = "The receiver account number is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Receiver account number property cannot be empty\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Missing amount",
                                            description = "The amount of transaction is missing",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Amount is required\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid amount",
                                            description = "The amount of transaction is lower or equal than 0",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"The amount of money to deposit must be larger than 0\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Internal server error",
                                            description = "The internal error occur on the server when running api",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Internal server error message\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Service provider ID not found",
                                            description = "The service provider ID provided not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Provider not found\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Receiver not found",
                                            description = "The receiver account number provided not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Receiver not found\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}")
                            })
            )
    })
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<EmployeeTransaction>> deposit(@Valid @RequestBody EmployeeTransactionDTO request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                EmployeeTransaction created = transactionService.createEmployeeTransaction(request);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,  List.of("Employee Transaction created!"), created));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        }
    }

    @Operation(
            summary = "Get All Employee Transaction - Get All Deposit",
            description = "Receive a list of all employee transactions (deposits for specific account) from the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get All Transactions Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Get all employee transactions successfully\",\n" +
                                            "  \"data\": \"List{EmployeeTransaction{id='string', amount='int', createdAt='time'}}\"\n" +
                                            "}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Transactions Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find any employee transactions\",\n" +
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

    @Operation(
            summary = "Get Employee Transaction - Deposit By Service Provider ID",
            description = "Receive a list of employee transaction details (deposits for specific account) based on the provided service provider ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Transactions Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Get all employee transactions successfully\",\n" +
                                            "  \"data\": \"List{EmployeeTransaction{id='string', amount='int', createdAt='time'}}\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Service Provider Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find service provider with id: 550e8400-e29b-41d4-a716-446655440000\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Transactions Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find any employee transactions\",\n" +
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
            name = "id",
            description = "The id of the service provider whose deposit history being got",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
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

    @Operation(
            summary = "Get Employee Transaction - Deposit By Account ID",
            description = "Receive a list of employee transaction details (deposits for specific account) based on the provided account ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Transactions Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Get all employee transactions successfully\",\n" +
                                            "  \"data\": \"List{EmployeeTransaction{id='string', amount='int', createdAt='time'}}\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Account Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find account with id: 550e8400-e29b-41d4-a716-446655440000\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Transactions Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find any employee transactions\",\n" +
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
            name = "id",
            description = "The id of the account whose deposit history being got",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
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

    @Operation(
            summary = "Get Transaction By Account ID",
            description = "Receive a list of transaction details based on the provided account ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Transactions Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Get all transactions successfully\",\n" +
                                            "  \"data\": \"List{Transaction{amount='int', description='string', type='string', createdAt='time'}}\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Account Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find account with id: 550e8400-e29b-41d4-a716-446655440000\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Transactions Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find any transactions\",\n" +
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
            name = "id",
            description = "The id of the account whose transaction history being got",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
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

    @Operation(
            summary = "Get Dept Transaction By Account ID",
            description = "Receive a list of dept transaction details based on the provided account ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Transactions Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Get all dept transactions successfully\",\n" +
                                            "  \"data\": \"List{Transaction{amount='int', description='string', type='string', createdAt='time'}}\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Account Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find account with id: 550e8400-e29b-41d4-a716-446655440000\",\n" +
                                            "  \"data\": \"null\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Transactions Not Found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": false,\n" +
                                            "  \"message\": \"Cannot find any dept transactions\",\n" +
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
            name = "id",
            description = "The id of the account whose dept transaction history being got",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
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

    @Operation(
            summary = "Get All Transactions",
            description = "Receive a list of all transactions from the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get All Transactions Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"response\": [\"List{AllTransactionResponse{sendBank='string', receiveBank='string', amount='string', sendingAccount='string', receivingAccount='string', date='string', action='string'}}\"]\n" +
                                            "}")))
    })
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
