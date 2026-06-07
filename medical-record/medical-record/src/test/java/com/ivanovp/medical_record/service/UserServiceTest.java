package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.response.UserResponseDTO;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void getAllUsers_filtersOutAdminUsers() {
        // Arrange
        User admin = buildUser(1L, "admin", UserRole.ADMIN);
        User doctor = buildUser(2L, "doctor1", UserRole.DOCTOR);
        User patient = buildUser(3L, "patient1", UserRole.PATIENT);
        when(userRepository.findAll()).thenReturn(List.of(admin, doctor, patient));

        // Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).noneMatch(u -> u.getRole().equals("ADMIN"));
    }

    @Test
    void getAllUsers_includesNonAdminUsers() {
        // Arrange
        User doctor = buildUser(2L, "doctor1", UserRole.DOCTOR);
        when(userRepository.findAll()).thenReturn(List.of(doctor));

        // Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("doctor1");
        assertThat(result.get(0).getRole()).isEqualTo("DOCTOR");
    }

    @Test
    void getAllUsers_whenOnlyAdminsExist_returnsEmptyList() {
        // Arrange
        User admin = buildUser(1L, "admin", UserRole.ADMIN);
        when(userRepository.findAll()).thenReturn(List.of(admin));

        // Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void deleteUser_whenUserExists_deletesSuccessfully() {
        // Arrange
        User user = buildUser(1L, "doctor1", UserRole.DOCTOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_whenUserNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    private User buildUser(Long id, String username, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        return user;
    }
}
