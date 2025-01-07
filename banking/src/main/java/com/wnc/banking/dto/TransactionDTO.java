package com.wnc.banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "012345678910")
    @NotNull(message = "Sender account number is required")
    private String senderAccountNumber;

    @Schema(example = "010987654321")
    @NotNull(message = "Receiver account number is required")
    private String receiverAccountNumber;

    @Schema(example = "100000")
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount property must be larger than 0")
    private Long amount;

    @Schema(example = "description")
    private String description;

    @Schema(example = "internal")
    @NotNull(message = "Type of transaction is required")
    @Pattern(regexp = "internal|external|dept", message = "Type must be one of the following: internal, external, dept")
    private String type;
}
