package com.gussbom.auth.services;

import com.gussbom.auth.dtos.requests.RegistrationRequest;
import com.gussbom.auth.dtos.requests.LoginRequest;
import com.gussbom.auth.dtos.responses.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Component
public interface AuthServices {

    GenericResponse userLogin(LoginRequest request);
    GenericResponse userRegistration(RegistrationRequest request);
    GenericResponse confirmOtp();
    GenericResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
