package com.ivanovp.medical_record.service.impl;

import com.ivanovp.medical_record.dto.request.UserLoginDTO;
import com.ivanovp.medical_record.dto.request.UserRegisterDTO;
import com.ivanovp.medical_record.dto.response.JwtAuthResponseDTO;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import com.ivanovp.medical_record.repository.PatientRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements com.ivanovp.medical_record.service.AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtAuthResponseDTO register(UserRegisterDTO dto) {
        // Check if username is already taken
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + dto.getUsername() + "' is already taken");
        }

        // Create User entity
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.PATIENT);
        user = userRepository.save(user);

        // Create Patient entity
        Patient patient = new Patient();
        patient.setEgn(dto.getEgn());
        patient.setName(dto.getName());
        patient.setInsured(true);
        patient.setUser(user);
        patientRepository.save(patient);

        // Generate JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String token = jwtTokenProvider.generateToken(userDetails);

        return new JwtAuthResponseDTO(token, user.getUsername(), user.getRole().name());
    }
    @Override
    public JwtAuthResponseDTO login(UserLoginDTO dto) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        // Load UserDetails and generate JWT token
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

                //customUserDetailsService.loadUserByUsername()
        String token = jwtTokenProvider.generateToken(userDetails);

        return new JwtAuthResponseDTO(token, user.getUsername(), user.getRole().name());
    }
}

