package com.codearena.codearena.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserRequest {

@Schema(
        description = "User's display name",
        example = "Abhishek"
)
@NotBlank(message = "Name is required")
private String name;
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(
        description = "User's email address",
        example = "abhishek@example.com"
)
@NotBlank(message = "Email is required")
@Email(message = "Email must be valid")
private String email;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

  @Schema(
        description = "User password",
        example = "StrongPass123"
)
@NotBlank(message = "Password is required")
@Size(
        min = 8,
        message = "Password must contain at least 8 characters"
)
@Schema(
        description = "User password",
        example = "StrongPass123",
        writeOnly = true
)
private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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