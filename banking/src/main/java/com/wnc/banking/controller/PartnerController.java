package com.wnc.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnc.banking.dto.ApiResponse;
//import com.wnc.banking.dto.DepositRequest;
import com.wnc.banking.dto.DepositRequest;
import com.wnc.banking.dto.GetAccountInfoRequest;
import com.wnc.banking.dto.PartnerGetAccountResponse;
import com.wnc.banking.entity.ExternalTransaction;
import com.wnc.banking.entity.PartnerBank;
import com.wnc.banking.service.HmacService;
import com.wnc.banking.service.PartnerService;
import com.wnc.banking.service.SignatureService;
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

            ApiResponse<ExternalTransaction> responseData = new ApiResponse<>(true, List.of("Deposit to account: " + body.getAccountNumber() + " successfully!"),partnerService.partnerDeposit(body.getForeignAccountNumber(), body.getAccountNumber(), bank, body.getAmount(), signature));

            String jsonResponse = objectMapper.writeValueAsString(responseData);
            String ourSignature = Base64.getEncoder().encodeToString(signatureService.signData(jsonResponse.getBytes(), privateKey));

            return ResponseEntity.status(200).header("RSA-Signature", ourSignature).body(responseData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }
}
