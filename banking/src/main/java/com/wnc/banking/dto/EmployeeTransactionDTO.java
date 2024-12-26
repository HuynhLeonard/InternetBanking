package com.wnc.banking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTransactionDTO {
    @NotNull(message = "serviceProviderId property cannot be empty")
    private String serviceProviderId;
    @NotNull(message = "receiverAccountNumber property cannot be empty")
    private String receiverAccountNumber;
    @Min(value = 0, message = "The amount of money to deposit must be larger than 0")
    private Long amount;
}
