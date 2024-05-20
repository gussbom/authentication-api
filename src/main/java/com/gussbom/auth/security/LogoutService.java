package com.gussbom.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gussbom.auth.dtos.responses.GenericResponse;
import com.gussbom.auth.dtos.responses.LogoutResponse;
import com.gussbom.auth.entities.AppUser;
import com.gussbom.auth.repositories.TokenRepository;
import com.gussbom.auth.services.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final UserServices userServices;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String authHeader = request.getHeader("Authorization");
        final String token;


        try {
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }

        token = authHeader.substring(7);

        var storedToken = tokenRepository.findByToken(token).orElse(null);
        assert storedToken != null;
        AppUser user = userServices.findByEmail(storedToken.getUser().getEmail());
        var validTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validTokens.isEmpty())
            return;
        validTokens.forEach(t-> {
            t.setExpired(true);
        });
        tokenRepository.saveAll(validTokens);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        GenericResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Logged Out")
                                .time(LocalDateTime.now().toString())
                                .status(HttpStatus.OK)
                                .build()
                )
        );
        } catch (IOException e) {
            try {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json");
                response.getWriter().write(
                        new ObjectMapper().writeValueAsString(
                                GenericResponse.builder()
                                        .statusCode(HttpStatus.BAD_REQUEST.value())
                                        .message(e.getMessage())
                                        .time(LocalDateTime.now().toString())
                                        .status(HttpStatus.BAD_REQUEST)
                                        .build()
                        )
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
