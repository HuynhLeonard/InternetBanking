package com.wnc.banking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String senderAccountName;
    private String senderAccountNumber;
    private String receiverAccountName;
    private String receiverAccountNumber;
    private Long amount;
    private String description;
    private String type;
    private Instant createdAt;

}
