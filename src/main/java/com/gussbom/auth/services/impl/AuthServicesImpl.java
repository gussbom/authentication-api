package com.gussbom.auth.services.impl;

import com.gussbom.auth.dtos.requests.AuthRequest;
import com.gussbom.auth.dtos.requests.LoginRequest;
import com.gussbom.auth.dtos.responses.GenericResponse;
import com.gussbom.auth.dtos.responses.LoginResponse;
import com.gussbom.auth.entities.AppUser;
import com.gussbom.auth.entities.Token;
import com.gussbom.auth.exceptions.ResourceExistsException;
import com.gussbom.auth.exceptions.ResourceNotFoundException;
import com.gussbom.auth.repositories.AppUserRepository;
import com.gussbom.auth.repositories.TokenRepository;
import com.gussbom.auth.services.AuthServices;
import com.gussbom.auth.services.EmailServices;
import com.gussbom.auth.utils.OtpGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthServicesImpl implements AuthServices {

    private final AppUserRepository appUserRepository;
    private final TokenRepository tokenRepository;
    private final EmailServices emailServices;
    @Override
    public GenericResponse userLogin(LoginRequest request) {

        AppUser appUser = findByUsername(request.getUsername());

        LoginResponse response = LoginResponse.builder()
                .build();

        return GenericResponse.builder()
                .time(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Logged in.")
                .object(response)
                .build();
    }

    @Override
    public GenericResponse userRegistration(AuthRequest request) {

        boolean exists = confirmExistsByEmail(request.getEmail());
        if(exists){
            throw new ResourceExistsException("User already exists.");
        }

        AppUser appUser = AppUser.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .confirmationStatus(false)
                .build();
        appUserRepository.save(appUser);

        String token = OtpGenerator.generate();

        Token newToken = Token.builder()
                .token(token)
//                .expirationTime()
                .build();
        tokenRepository.save(newToken);

//        Send a mail to the user here.
        emailServices.sendMessage(appUser, token);

        return GenericResponse.builder()
                .time(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Registration Successful."+"\n"+
                         "Please check your mail for a confirmation OTP/Link.")
                .object(token)
                .build();
    }

    @Override
    public GenericResponse confirmOtp() {

        return GenericResponse.builder()
                .build();
    }

    @Override
    public GenericResponse userLogout() {

        return GenericResponse.builder()
                .build();
    }

    private AppUser findByEmail(String email){
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private AppUser findByUsername(String username){
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean confirmExistsByEmail(String email){
        return appUserRepository.existsByEmail(email);
//        if(confirmUserExists){
//            throw new ResourceExistsException("User already exists.");
//        }
    }
}
