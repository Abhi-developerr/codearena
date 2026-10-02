package com.codearena.codearena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

@Schema(
        description = "Current password of the authenticated user",
        example = "OldPass123"
)
@NotBlank(message = "Old password is required")
private String oldPassword;

    @Schema(
        description = "New password for the authenticated user",
        example = "NewStrongPass123"
    )
    @NotBlank(message = "New password is required")
    @Size(
        min = 8,
        message = "New password must contain at least 8 characters"
    )
private String newPassword;

    public ChangePasswordRequest() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}