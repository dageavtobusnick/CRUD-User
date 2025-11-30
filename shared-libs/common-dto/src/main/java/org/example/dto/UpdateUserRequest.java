package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Request for updating an existing user")
public class UpdateUserRequest {

    @Schema(
            description = "User's full name",
            example = "John Smith",
            required = true,
            minLength = 1
    )
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Schema(
            description = "User's email address",
            example = "john.smith@example.com",
            required = true
    )
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(
            description = "User's age",
            example = "35",
            required = true,
            minimum = "0"
    )
    @NotNull(message = "Age is mandatory")
    @PositiveOrZero(message = "Age must be positive or zero")
    private Integer age;

    // Constructors
    public UpdateUserRequest() {}

    public UpdateUserRequest(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
