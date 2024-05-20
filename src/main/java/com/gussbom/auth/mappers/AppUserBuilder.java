package com.gussbom.auth.mappers;

import com.gussbom.auth.dtos.requests.RegistrationRequest;
import com.gussbom.auth.entities.AppUser;

public class AppUserBuilder {

    public static AppUser buildAppUser(RegistrationRequest request){
        return AppUser.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .confirmationStatus(false)
                .build();
    }
}
