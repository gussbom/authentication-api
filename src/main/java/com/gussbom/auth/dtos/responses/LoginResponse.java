package com.gussbom.auth.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String bearerToken;
    private String refreshToken;
}
