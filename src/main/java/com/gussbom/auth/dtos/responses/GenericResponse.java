package com.gussbom.auth.dtos.responses;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class GenericResponse {
    private LocalDateTime time;
    private int statusCode;
    private HttpStatus status;
    private String message;
    private Object object;
}
