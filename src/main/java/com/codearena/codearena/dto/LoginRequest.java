package com.codearena.codearena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class LoginRequest {

   @Schema(
        description = "Registered user email",
        example = "abhishek@example.com"
)
@NotBlank(message = "Email is required")
@Email(message = "Email must be valid")
private String email;


@Schema(
        description = "User password",
        example = "StrongPass123",
        writeOnly = true
)
@NotBlank(message = "Password is required")
private String password;

    public LoginRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}