package com.wnc.banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class GetAccountInfoRequest {
    @Schema(example = "1")
    private Long bankId;

    @Schema(example = "2025-01-01T00:00:00.000Z")
    private Instant timestamp;

    @Schema(example = "012345678910")
    private String accountNumber;
}
