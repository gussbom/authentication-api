package com.gussbom.auth.controllers;

import com.gussbom.auth.dtos.requests.AuthRequest;
import com.gussbom.auth.dtos.requests.LoginRequest;
import com.gussbom.auth.dtos.responses.GenericResponse;
import com.gussbom.auth.services.AuthServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("auth-api")
public class AuthController {

    private final AuthServices authServices;

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> userRegistration(@RequestBody AuthRequest request){
        GenericResponse response = authServices.userRegistration(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<GenericResponse> userLogin(@RequestBody LoginRequest request){
        GenericResponse response = authServices.userLogin(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<GenericResponse> refreshToke(@RequestBody LoginRequest request){
//        GenericResponse response = authServices.userLogin(request);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//    @PostMapping("/reset-password")
//    public ResponseEntity<GenericResponse> resetPassword(@RequestBody LoginRequest request){
//        GenericResponse response = authServices.userLogin(request);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//    @PostMapping("/change-password")
//    public ResponseEntity<GenericResponse> changePassword(@RequestBody LoginRequest request){
//        GenericResponse response = authServices.userLogin(request);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
