package com.gussbom.auth.services;

import com.gussbom.auth.dtos.requests.RegistrationRequest;
import com.gussbom.auth.dtos.requests.LoginRequest;
import com.gussbom.auth.dtos.responses.GenericResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthServices {

    GenericResponse userLogin(LoginRequest request);
    GenericResponse userRegistration(RegistrationRequest request);
    GenericResponse confirmOtp();
    GenericResponse userLogout();
}
