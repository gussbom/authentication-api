package com.gussbom.auth.services.impl;

import com.gussbom.auth.entities.AppUser;
import com.gussbom.auth.exceptions.ResourceNotFoundException;
import com.gussbom.auth.repositories.AppUserRepository;
import com.gussbom.auth.services.GenericServices;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GenericServicesImpl implements GenericServices {

    private final AppUserRepository appUserRepository;
    public AppUser findByEmail(String email){
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public AppUser findByUsername(String username){
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public boolean existsByEmail(String email){
        return appUserRepository.existsByEmail(email);
    }
}
