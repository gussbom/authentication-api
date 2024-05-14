package com.gussbom.auth.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {
//    @NotEmpty(message="username cannot be empty")
    private String username;
    private String email;
    @NotEmpty(message="username cannot be empty")
    private String password;
}
