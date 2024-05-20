package com.gussbom.auth.controllers;

import com.gussbom.auth.dtos.responses.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("hello")
public class HelloController {

    @GetMapping("/sayHello")
    public ResponseEntity<GenericResponse> sayHello(){
        GenericResponse response = GenericResponse.builder()
                .message("Hello User")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
