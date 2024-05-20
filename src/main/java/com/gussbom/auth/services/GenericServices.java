package com.gussbom.auth.services;

import com.gussbom.auth.entities.AppUser;
import org.springframework.stereotype.Component;

@Component
public interface GenericServices {
    AppUser findByEmail(String email);
    AppUser findByUsername(String username);
    boolean existsByEmail(String email);
}
