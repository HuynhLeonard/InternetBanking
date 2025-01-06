package com.wnc.banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ik5ndXllbiBWYW4gQWkiLCJleHBpcmVkVGltZSI6MTcwNTExMjAwMH0.kjlsda8dhKasZ3lfTRF0xmDOqnEeq4Alp1T6ST5Ou_o")
    @NotNull(message = "Refresh token is required")
    private String refreshToken;
}
