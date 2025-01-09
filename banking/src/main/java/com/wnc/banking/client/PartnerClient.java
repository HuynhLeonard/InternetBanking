package com.wnc.banking.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnc.banking.dto.*;
import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.ExternalTransaction;
import com.wnc.banking.repository.AccountRepository;
import com.wnc.banking.repository.ExternalTransactionRepository;
import com.wnc.banking.repository.PartnerBankRepository;
import com.wnc.banking.service.HmacService;
import com.wnc.banking.service.SignatureService;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.PrivateKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

@Service
public class PartnerClient {
    @Autowired
    private ObjectMapper jacksonObjectMapper;
    private RestTemplate restTemplate;
    private final String partnerUrl = "http://localhost:3001/api/v1/partner-bank";

    @Autowired
    private HmacService hmacService;

    @Autowired
    private SignatureService signatureService;
    private String secretKey = "HHbank";

    @Autowired
    private PrivateKey privateKey;
    @Qualifier("objectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GenericResponseService responseBuilder;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PartnerBankRepository bankRepository;
    @Autowired
    private ExternalTransactionRepository externalTransactionRepository;

    @Autowired
    public PartnerClient(RestTemplateBuilder restTemplateBuilder, ObjectMapper jacksonObjectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public ApiResponsePartnerTeam3 deposit(ExternalDepositRequest request) throws Exception {
        Account account = accountRepository.findByAccountNumber(request.getSrcAccountNumber());
        if (account == null) {
            throw new Exception("Source account not found");
        }
        HttpHeaders headers = new HttpHeaders();

        String url = partnerUrl + "/external-transfer-rsa";

        Instant time = Instant.now().plusSeconds(300);

//        ExternalDepositRequest requestBody = new ExternalDepositRequest();
//        requestBody.setSrcBankCode("BANK1");
//        requestBody.setDesAccountNumber("301241883974");
//        requestBody.setAmount(1000L);
//        requestBody.setDescription("Sending money from team 3");
//        requestBody.setExp(time);
//        requestBody.setSrcAccountNumber("025376406887");
//        requestBody.setIsSourceFee(false);
        request.setExp(time);
        String jsonBody = jacksonObjectMapper.writeValueAsString(request);

        String hashed = hmacService.generateHmac(jacksonObjectMapper.writeValueAsString(request), secretKey);
//        System.out.println(jacksonObjectMapper.writeValueAsString(requestBody));
        headers.set("hashedData", hashed);

        String signature = Base64.getEncoder().encodeToString(signatureService.signData(jsonBody.getBytes(), privateKey));

        ExternalDepositRequestToTeam3 realRequest = objectMapper.convertValue(request, ExternalDepositRequestToTeam3.class);
        //realRequest.setExp(time);
        realRequest.setSignedData(signature);

        HttpEntity<ExternalDepositRequestToTeam3> entity = new HttpEntity<>(realRequest,headers);
        try {
            ResponseEntity<ApiResponsePartnerTeam3> response = restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                account.setBalance(account.getBalance() - request.getAmount());
            }

            ExternalTransaction transaction = new ExternalTransaction();
            transaction.setAmount(request.getAmount());
            transaction.setBank(bankRepository.findPartnerBankById(1L));
            transaction.setCreatedAt(Instant.now());
            transaction.setAccountNumber(request.getDesAccountNumber());
            transaction.setForeignAccountNumber(request.getSrcAccountNumber());
            transaction.setTheirSignature(objectMapper.convertValue(response.getBody().getData(), ExternalTransferData.class).getSignedData());
            transaction.setType("Send");
            externalTransactionRepository.save(transaction);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            try {
                String body = e.getResponseBodyAsString();
                System.out.println(body);
                return jacksonObjectMapper.readValue(body, ApiResponsePartnerTeam3.class);
            } catch (Exception parseException) {
                System.out.println(0);
                return null;
            }
        }
    }

    public ApiResponsePartnerTeam3 query(QueryPartnerCustomerRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        String url = partnerUrl + "/get-account-information";

        QueryPartnerCustomerRequestTeam3 requestBody = objectMapper.convertValue(request, QueryPartnerCustomerRequestTeam3.class);
        Instant time = Instant.now().plusSeconds(3000);
        requestBody.setExp(time);
        String hashed = hmacService.generateHmac(jacksonObjectMapper.writeValueAsString(requestBody), secretKey);
        headers.set("hashedData", hashed);

        HttpEntity<QueryPartnerCustomerRequestTeam3> entity = new HttpEntity<>(requestBody,headers);
        try {
            ResponseEntity<ApiResponsePartnerTeam3> response = restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                    }
            );
            System.out.println(jacksonObjectMapper.writeValueAsString(response.getBody()));

            return response.getBody();
        } catch (HttpClientErrorException e) {
                String body = e.getResponseBodyAsString();
                System.out.println(body);
                ApiResponsePartnerTeam3 realResponse = objectMapper.readValue(body, ApiResponsePartnerTeam3.class);
                return realResponse;
        }
    }
}
