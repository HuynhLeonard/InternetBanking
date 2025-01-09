package com.wnc.banking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class ExternalDepositRequest {
    private String srcAccountNumber;
    private String srcBankCode;
    private String desAccountNumber;
    private Long amount;
    private String description;
    private Boolean isSourceFee;
    private Instant exp;
}