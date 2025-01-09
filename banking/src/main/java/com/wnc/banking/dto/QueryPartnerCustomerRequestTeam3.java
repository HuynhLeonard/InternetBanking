package com.wnc.banking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class QueryPartnerCustomerRequestTeam3 {
    private String srcBankCode;
    private String desAccountNumber;
    private Instant exp;
}