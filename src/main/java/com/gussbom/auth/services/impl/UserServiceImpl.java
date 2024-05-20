package com.gussbom.auth.services.impl;

import com.gussbom.auth.entities.AppUser;
import com.gussbom.auth.exceptions.ResourceNotFoundException;
import com.gussbom.auth.repositories.AppUserRepository;
import com.gussbom.auth.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServices {
    private final AppUserRepository appUserRepository;
    public AppUser findByEmail(String email){
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public AppUser findByUsername(String username){
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public AppUser findCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
