package com.gussbom.auth.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LogoutResponse {
    private String message;
    private String time;
}
