package com.gussbom.auth.services.impl;

import com.gussbom.auth.entities.AppUser;
import com.gussbom.auth.services.EmailServices;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailServices {
    @Override
    public void sendMessage(AppUser appUser, String otp) {

    }
}
