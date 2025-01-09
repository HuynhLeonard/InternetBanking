package com.wnc.banking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private String bankId;
    @NotNull
    private String senderAccountNumber;
    @NotNull
    private String receiverAccountNumber;
    @NotNull
    @Min(value = 1, message = "amount property must be larger than 0")
    private Long amount;
    private String description;
    @NotNull
    @Pattern(regexp = "internal|external|dept", message = "Type must be one of the following: internal, external, dept")
    private String type;
}
