package com.gussbom.auth.services;

import com.gussbom.auth.entities.AppUser;
import org.springframework.stereotype.Component;

@Component
public interface EmailServices {

    void sendMessage(AppUser appUser, String otp);
}
