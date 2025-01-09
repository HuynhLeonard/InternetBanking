package com.wnc.banking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

//import java.time.Instant;

@Getter
@Setter
public class GetAccountInfoRequest {
    private Long bankId;
    private Instant timestamp;
    private String accountNumber;
}
