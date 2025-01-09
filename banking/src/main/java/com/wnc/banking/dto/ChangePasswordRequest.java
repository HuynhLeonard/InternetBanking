package com.wnc.banking.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
    @Schema(example = "old password")
    @NotNull(message = "Old password is required")
    @Size(min = 8, max = 24, message = "Old password must be between 8 and 24 characters")
    private String oldPassword;

    @Schema(example = "new password")
    @NotNull(message = "New password is required")
    @Size(min = 8, max = 24, message = "New password must be between 8 and 24 characters")
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String email, String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
