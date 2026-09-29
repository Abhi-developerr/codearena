package com.codearena.codearena.dto;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserResponse {
    
    @Schema(
        description = "Unique user ID",
        example = "1"
)
private Long id;

@Schema(
        description = "User's display name",
        example = "Abhishek"
)
private String name;

@Schema(
        description = "User's registered email",
        example = "abhishek@example.com"
)
private String email;

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

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
}
