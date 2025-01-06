package com.wnc.banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @Schema(example = "customer@gmail.com")
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @Schema(example = "password")
    @NotNull(message = "Password is required")
    private String password;
}
