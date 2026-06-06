package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.request.UserLoginDTO;
import com.ivanovp.medical_record.dto.request.UserRegisterDTO;
import com.ivanovp.medical_record.dto.response.JwtAuthResponseDTO;
import com.ivanovp.medical_record.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponseDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDTO> login(@Valid @RequestBody UserLoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
