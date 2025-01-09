package com.wnc.banking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerGetAccountResponse {
    private String customerName;
    private Long balance;
}
