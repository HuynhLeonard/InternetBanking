package com.wnc.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnc.banking.client.PartnerClient;
import com.wnc.banking.dto.*;
//import com.wnc.banking.dto.DepositRequest;
import com.wnc.banking.dto.DepositRequest;
import com.wnc.banking.dto.GetAccountInfoRequest;
import com.wnc.banking.dto.PartnerGetAccountResponse;
import com.wnc.banking.entity.ExternalTransaction;
import com.wnc.banking.entity.PartnerBank;
import com.wnc.banking.service.HmacService;
import com.wnc.banking.service.PartnerService;
import com.wnc.banking.service.SignatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.service.RequestBodyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Tag(name = "Partner", description = "Endpoints for managing partner - the other banks")
@RestController
@RequestMapping("/api/external")
@AllArgsConstructor
public class PartnerController {
    private final RequestBodyService requestBodyBuilder;
    private PartnerService partnerService;
    private HmacService hmacService;
    private ObjectMapper objectMapper;

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SignatureService signatureService;
    private PartnerClient clientTeam3;

    @Operation(
            summary = "Get Customer Profile For Partner",
            description = "Receive customer details for partner based on the provided request and header"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Get Customer Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Get the customer info successfully\",\n" +
                                            "  \"data\": \"PartnerGetAccountResponse{customerName='string', balance='string'}\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Expired request",
                                            description = "The request sent from partner is expired",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Expired request\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Bank not found",
                                            description = "The bank id provided is not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Bank not found\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid request",
                                            description = "The request sent from partner edited",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Request edited by untrusted source\",\n" +
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
    @PostMapping("/customer")
    public ResponseEntity<ApiResponse<?>> getCustomer(@RequestBody GetAccountInfoRequest body, @RequestHeader("HMAC") String hashed) {
        try {
            Instant timestampNow = Instant.now();

            if (timestampNow.getEpochSecond() - body.getTimestamp().getEpochSecond() > 120) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Expired request"), null));
            }

            PartnerBank bank = partnerService.getPartnerBank(body.getBankId());

            if (bank == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Bank not found"), null));
            }

            String secretKey = bank.getLocalSecretKey();

            String jsonBodyString = objectMapper.writeValueAsString(body);

            System.out.println(secretKey);
            System.out.println(jsonBodyString);
            System.out.println(hmacService.generateHmac(jsonBodyString, "HHbank"));
            boolean isNotEdited = hmacService.isHmacValid(jsonBodyString, hashed, secretKey);

            if (!isNotEdited) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Request edited by untrusted source"), null));
            }

            PartnerGetAccountResponse responseData = partnerService.getAccountInformation(body.getAccountNumber());
            return ResponseEntity.status(200).body(new ApiResponse<>(true, List.of("Get the customer info successfully"), responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @Operation(
            summary = "Transaction With Account From Partner",
            description = "Transaction money from internal account to account from partner based on the provided request and header"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Account Transaction Successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"success\": true,\n" +
                                            "  \"message\": \"Deposit to account: 012345678910 successfully!\",\n" +
                                            "  \"data\": \"ExternalTransaction{accountNumber='string', amount='long', type='string', createdAt='time', foreignAccountNumber='string'}\"\n" +
                                            "}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Expired request",
                                            description = "The request sent from partner is expired",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Expired request\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Bank not found",
                                            description = "The bank id provided is not exists in the system",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Bank not found\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid request",
                                            description = "The request sent from partner edited",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Request edited by untrusted source\",\n" +
                                                    "  \"data\": \"null\"\n" +
                                                    "}"),
                                    @ExampleObject(
                                            name = "Invalid signature",
                                            description = "The signature provided is not correct",
                                            value = "{\n" +
                                                    "  \"success\": false,\n" +
                                                    "  \"message\": \"Invalid signature\",\n" +
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
    @PostMapping("/customer/deposit")
    public ResponseEntity<ApiResponse<?>> deposit(@RequestBody DepositRequest body, @RequestHeader("HMAC") String hashed, @RequestHeader("RSA-Signature") String signature) {
        try {
            Instant timestampNow = Instant.now();

            if (timestampNow.getEpochSecond() - body.getTimestamp().getEpochSecond() > 120) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Expired request"), null));
            }

            PartnerBank bank = partnerService.getPartnerBank(body.getBankId());

            if (bank == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Bank not found"), null));
            }

            String secretKey = bank.getLocalSecretKey();

            String jsonBodyString = objectMapper.writeValueAsString(body);

            boolean isNotEdited = hmacService.isHmacValid(jsonBodyString, hashed, secretKey);

            if (!isNotEdited) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Request edited by untrusted source"), null));
            }

            System.out.println(signature);

            byte[] byteSignature = Base64.getDecoder().decode(signature);

            if (!signatureService.verifyData(jsonBodyString.getBytes(), byteSignature, publicKey))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of("Invalid signature"), null));
            partnerService.partnerDeposit(body.getForeignAccountNumber(), body.getAccountNumber(), bank, body.getAmount(), signature);
            ApiResponse<?> responseData = new ApiResponse<>(true, List.of("Deposit to account: " + body.getAccountNumber() + " successfully!"), "Successfully!");

            String ourSignature = Base64.getEncoder().encodeToString(signatureService.signData("Successfully!".getBytes(), privateKey));

            return ResponseEntity.status(200).header("RSA-Signature", ourSignature).body(responseData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @PostMapping("/customer/query")
    public ResponseEntity<ApiResponse<?>> getPartnerBankCustomer(@RequestBody QueryPartnerCustomerRequest request) {
        try {
            ApiResponse response = new ApiResponse();
            ApiResponsePartnerTeam3 responseQuery = clientTeam3.query(request);
            response.setData(responseQuery.getData());
            response.setSuccess(responseQuery.getSuccess());
            if (responseQuery.getErrors() == null) {
                List<String> message =  List.of("Get partner account info successfully");
                response.setMessage(message);
            } else {
                List<Team3Errors> errors = responseQuery.getErrors();
                response.setMessage(List.of(errors.getFirst().getMessage()));
            }
//            List<String> message = responseQuery.getErrors() == null ? List.of("Get partner account info successfully") : responseQuery.getErrors();
//            response.setMessage(message);
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }


//    @PostMapping("/customer/deposit-to-external")
//    public ResponseEntity<ApiResponse<?>> depositToExternal(@RequestBody ExternalDepositRequest requestBody) {
//        try {
//            ApiResponse response = new ApiResponse();
//            ApiResponsePartnerTeam3 responseQuery = clientTeam3.deposit(requestBody);
//            response.setData(responseQuery.getData());
//            response.setSuccess(responseQuery.getSuccess());
//            if (responseQuery.getErrors() == null) {
//                List<String> message =  List.of("Deposit success fully");
//                response.setMessage(message);
//            } else {
//                List<Team3Errors> errors = responseQuery.getErrors();
//                response.setMessage(List.of(errors.getFirst().getMessage()));
//            }
//            return ResponseEntity.status(200).body(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
//        }
//    }
}