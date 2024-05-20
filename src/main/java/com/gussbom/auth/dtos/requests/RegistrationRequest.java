package com.gussbom.auth.dtos.requests;

import com.gussbom.auth.enums.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

@Data
@Valid
public class RegistrationRequest {
    @NotEmpty(message = "username cannot be empty")
    private String username;
    @NotEmpty(message = "email cannot be empty")
    @Email(message = "enter a valid email")
    private String email;
    @NotEmpty(message = "password cannot be empty")
    private String password;
    private UserRole role;
}
