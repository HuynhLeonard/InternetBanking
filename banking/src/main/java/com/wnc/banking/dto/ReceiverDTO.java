package com.wnc.banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ReceiverDTO {
    @Schema(example = "012345678910")
    @NotNull(message = "Sender account number is required")
    @Pattern(regexp = "^0\\d{11}$", message = "Sender account number must have 12 digits and start with 0")
    private String senderAccountNumber;

    @NotNull(message = "Receiver account number is required", groups = OnCreateDTO.class)
    @Pattern(regexp = "^0\\d{11}$", message = "Receiver account number must have 12 digits and start with 0")
    @Schema(example = "010987654321")
    private String receiverAccountNumber;

    @Schema(example = "account nickname")
    private String nickName;
    private String type;
    private Integer bankId;

}
