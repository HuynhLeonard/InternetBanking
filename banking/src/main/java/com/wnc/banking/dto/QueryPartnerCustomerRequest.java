package com.wnc.banking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class QueryPartnerCustomerRequest {
    private String srcBankCode;
    private String desAccountNumber;
}
