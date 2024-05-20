package com.gussbom.auth.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest implements Serializable {
    private String username;
    private String email;
    @NotEmpty(message="password cannot be empty")
    private String password;
}
