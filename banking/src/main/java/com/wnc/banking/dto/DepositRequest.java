package com.wnc.banking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class DepositRequest {
    private Long bankId;
    private String accountNumber;
    private String foreignAccountNumber;
    private Long amount;
    private Instant timestamp;
}
