package com.gussbom.auth.services.impl;

import com.gussbom.auth.dtos.requests.RegistrationRequest;
import com.gussbom.auth.dtos.requests.LoginRequest;
import com.gussbom.auth.dtos.responses.GenericResponse;
import com.gussbom.auth.dtos.responses.LoginResponse;
import com.gussbom.auth.entities.AppUser;
import com.gussbom.auth.entities.Token;
import com.gussbom.auth.exceptions.BadRequestException;
import com.gussbom.auth.exceptions.ResourceExistsException;
import com.gussbom.auth.repositories.AppUserRepository;
import com.gussbom.auth.repositories.TokenRepository;
import com.gussbom.auth.services.AuthServices;
import com.gussbom.auth.services.EmailServices;
import com.gussbom.auth.services.GenericServices;
import com.gussbom.auth.utils.OtpGenerator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthServicesImpl implements AuthServices {

    private final AppUserRepository appUserRepository;
    private final TokenRepository tokenRepository;
    private final EmailServices emailServices;
    private final GenericServices genericServices;
    @Override
    public GenericResponse userLogin(LoginRequest request) {

        AppUser appUser = findAppUser(request);
//        remember to hash the request password before comparing.
        boolean confirmUserData = appUser.getPassword().equals(request.getPassword());
        if(!confirmUserData){
            String message = "Incorrect user details. Please input correct user details";
            throw new BadRequestException(message);
        }

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
    public GenericResponse userRegistration(RegistrationRequest request) {

        boolean exists = genericServices.existsByEmail(request.getEmail());
        if(exists){
            String message = "Email exists already, please register with a different email";
            throw new ResourceExistsException(message);
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
//                Ideally the token should not be part of the response, remove later on.
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

    private void confirmUserDataNotNull(LoginRequest request){
        boolean result = request.getEmail()!=null && request.getUsername()!=null;
        if(!result){
            String message = "Please input an Email or a Username";
            throw new BadRequestException(message);
        }

        boolean passwordResult = request.getPassword()!=null;
        if(!passwordResult){
            String message = "Please input a password";
            throw new BadRequestException(message);
        }
    }

    private AppUser findAppUser(LoginRequest request){
        confirmUserDataNotNull(request);
        AppUser appUser = new AppUser();
        if(appUser.getUsername()!=null){
            appUser = genericServices.findByUsername(request.getUsername());
        }else{
            appUser = genericServices.findByEmail(request.getEmail());
        }
        return appUser;
    }
}
