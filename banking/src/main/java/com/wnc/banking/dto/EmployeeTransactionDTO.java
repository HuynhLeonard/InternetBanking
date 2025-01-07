package com.wnc.banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Service Provider ID property cannot be empty")
    private String serviceProviderId;

    @Schema(example = "010987654321")
    @NotNull(message = "Receiver account number property cannot be empty")
    private String receiverAccountNumber;

    @Schema(example = "100000")
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "The amount of money to deposit must be larger than 0")
    private Long amount;
}
