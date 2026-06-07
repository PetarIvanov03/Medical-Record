package com.ivanovp.medical_record.security;

import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import com.ivanovp.medical_record.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetailsWithCorrectRole() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("doctor1");
        user.setPassword("encoded-password");
        user.setRole(UserRole.DOCTOR);

        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("doctor1");

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo("doctor1");
        assertThat(userDetails.getPassword()).isEqualTo("encoded-password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_DOCTOR");
    }

    @Test
    void loadUserByUsername_whenUserIsPatient_returnsPatientRole() {
        // Arrange
        User user = new User();
        user.setUsername("patient1");
        user.setPassword("encoded-password");
        user.setRole(UserRole.PATIENT);

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("patient1");

        // Assert
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_PATIENT");
    }

    @Test
    void loadUserByUsername_whenUserIsAdmin_returnsAdminRole() {
        // Arrange
        User user = new User();
        user.setUsername("admin");
        user.setPassword("encoded-password");
        user.setRole(UserRole.ADMIN);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        // Assert
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_whenUserNotFound_throwsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown");
    }
}
