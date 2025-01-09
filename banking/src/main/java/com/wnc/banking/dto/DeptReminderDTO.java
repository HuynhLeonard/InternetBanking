package com.wnc.banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeptReminderDTO {
    @Schema(example = "012345678910")
    @NotNull(message = "Sender account number is required")
    @Size(min = 10, max = 10, message = "Sender account number must be 10 characters")
    private String senderAccountNumber;

    @Schema(example = "010987654321")
    @NotNull(message = "Receiver account number is required")
    @Size(min = 10, max = 10, message = "Receiver account number must be 10 characters")
    private String receiverAccountNumber;

    @Schema(example = "100000")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be larger than 0")
    private Long amount;

    @Schema(example = "description")
    private String description;
}
