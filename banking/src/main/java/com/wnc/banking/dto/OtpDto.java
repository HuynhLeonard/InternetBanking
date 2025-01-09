package com.wnc.banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OtpDto {
    @Schema(example = "123456")
    private String otp;

    @Schema(example = "customer@gmail.com")
    private String email;
}
