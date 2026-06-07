package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.UserLoginDTO;
import com.ivanovp.medical_record.dto.request.UserRegisterDTO;
import com.ivanovp.medical_record.dto.response.JwtAuthResponseDTO;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import com.ivanovp.medical_record.repository.PatientRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.security.JwtTokenProvider;
import com.ivanovp.medical_record.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @InjectMocks private AuthServiceImpl authService;

    @Test
    void register_whenUsernameAvailable_returnsJwtAuthResponseWithPatientRole() {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO("newuser", "password123", "Test User", "1234567890");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encoded-pass");
        savedUser.setRole(UserRole.PATIENT);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");

        // Act
        JwtAuthResponseDTO result = authService.register(dto);

        // Assert
        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getRole()).isEqualTo("PATIENT");
    }

    @Test
    void register_whenUsernameAlreadyTaken_throwsIllegalArgumentException() {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO("existinguser", "password123", "Test", "1234567890");
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existinguser");
    }

    @Test
    void login_whenCredentialsValid_returnsJwtAuthResponseWithCorrectRole() {
        // Arrange
        UserLoginDTO dto = new UserLoginDTO("doctor1", "password123");

        User user = new User();
        user.setUsername("doctor1");
        user.setPassword("encoded-pass");
        user.setRole(UserRole.DOCTOR);

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");

        // Act
        JwtAuthResponseDTO result = authService.login(dto);

        // Assert
        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getUsername()).isEqualTo("doctor1");
        assertThat(result.getRole()).isEqualTo("DOCTOR");
    }
}
