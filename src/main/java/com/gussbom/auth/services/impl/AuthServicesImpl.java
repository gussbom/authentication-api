package com.gussbom.auth.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gussbom.auth.dtos.requests.RegistrationRequest;
import com.gussbom.auth.dtos.requests.LoginRequest;
import com.gussbom.auth.dtos.responses.GenericResponse;
import com.gussbom.auth.dtos.responses.LoginResponse;
import com.gussbom.auth.entities.AppUser;
import com.gussbom.auth.entities.Token;
import com.gussbom.auth.enums.TokenType;
import com.gussbom.auth.exceptions.ResourceExistsException;
import com.gussbom.auth.exceptions.ResourceNotFoundException;
import com.gussbom.auth.mappers.AppUserBuilder;
import com.gussbom.auth.repositories.AppUserRepository;
import com.gussbom.auth.repositories.TokenRepository;
import com.gussbom.auth.security.JwtService;
import com.gussbom.auth.services.AuthServices;
import com.gussbom.auth.services.EmailServices;
import com.gussbom.auth.services.GenericServices;
import com.gussbom.auth.services.UserServices;
import com.gussbom.auth.utils.OtpGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServicesImpl implements AuthServices {

    private final AppUserRepository appUserRepository;
    private final TokenRepository tokenRepository;
    private final EmailServices emailServices;
    private final GenericServices genericServices;
    private final UserServices userServices;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public GenericResponse userLogin(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            return GenericResponse.builder()
                    .time(LocalDateTime.now().toString())
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid credentials, please check details and try again.")
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        AppUser appUser = userServices.findByEmail(request.getEmail());

        var token = jwtService.generateToken(appUser);
        var refreshToken = jwtService.generateRefreshToken(appUser);
        revokeAllTokens(appUser);
        saveAppUserToken(appUser, token, TokenType.BEARER);
        saveAppUserToken(appUser, refreshToken, TokenType.REFRESH);

        LoginResponse response = LoginResponse.builder()
                .refreshToken(refreshToken)
                .bearerToken(token)
                .build();

        return GenericResponse.builder()
                .time(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Logged in.")
                .object(response)
                .build();
    }

    @Override
    @Transactional
    public GenericResponse userRegistration(RegistrationRequest request) {

        boolean exists = genericServices.existsByEmail(request.getEmail());
        if(exists){
            String message = "Email exists already, please register with a different email";
            throw new ResourceExistsException(message);
        }

        AppUser appUser = AppUserBuilder.buildAppUser(request);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        AppUser user = appUserRepository.save(appUser);

//        This is a registration confirmation token.
        String token = OtpGenerator.generate();
        saveAppUserToken(user, token, TokenType.CONFIRMATION);

//        Send a mail to the user here.
        emailServices.sendMessage(appUser, token);

        return GenericResponse.builder()
                .time(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Registration Successful, Please check your mail for a confirmation OTP/Link.")
                .build();
    }

    @Override
    public GenericResponse confirmOtp() {

        return GenericResponse.builder()
                .build();
    }

    @Override
    @Transactional
    public GenericResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return GenericResponse.builder()
                        .message("Null or invalid token")
                        .build();
            }

            refreshToken = authHeader.substring(7);

            Token token = tokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

            userEmail = jwtService.getUsernameFromToken(refreshToken);

            if (userEmail != null) {
                AppUser userDetails = userServices.findByEmail(userEmail);

                if (jwtService.validateToken(refreshToken, userDetails) && !token.isExpired()) {
                    var accessToken = jwtService.generateToken(userDetails);

                    revokeAllBearerTokens(userDetails);
                    saveAppUserToken(userDetails, accessToken, TokenType.BEARER);

                    var authResponse = LoginResponse.builder()
                            .bearerToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                    return GenericResponse.builder()
                            .time(LocalDateTime.now().toString())
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .message("Bearer token regenerated.")
                            .object(authResponse)
                            .build();
                } else {
                    token.setExpired(true);
                    throw new BadRequestException("Session Expired. Log in again");
                }
            }
            return GenericResponse.builder()
                    .time(LocalDateTime.now().toString())
                    .status(HttpStatus.BAD_REQUEST)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("User email is null")
                    .build();
        } catch (BadRequestException e) {
            return GenericResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .time(LocalDateTime.now().toString())
                    .message(e.getMessage())
                    .build();
        }
    }

    private void saveAppUserToken(AppUser user, String token, TokenType type){
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .expired(false)
                .type(type)
                .build();
        tokenRepository.save(newToken);
    }

    private void revokeAllTokens(AppUser user){
        var validTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validTokens.isEmpty())
            return;
        validTokens.forEach(t-> {
            t.setExpired(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    private void revokeAllBearerTokens(AppUser user){
        var validTokens = tokenRepository.findAllValidTokenByUserAndType(user.getId(), TokenType.BEARER);
        if(validTokens.isEmpty())
            return;
        validTokens.forEach(t-> {
            t.setExpired(true);
        });
        tokenRepository.saveAll(validTokens);
    }
}
