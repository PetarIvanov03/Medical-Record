package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_whenUserExists_returnsUser() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(UserRole.DOCTOR);
        userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByUsername("testuser");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getRole()).isEqualTo(UserRole.DOCTOR);
    }

    @Test
    void findByUsername_whenUserDoesNotExist_returnsEmpty() {
        // Act
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_isCaseSensitive() {
        // Arrange
        User user = new User();
        user.setUsername("CaseSensitive");
        user.setPassword("password");
        user.setRole(UserRole.PATIENT);
        userRepository.save(user);

        // Act
        Optional<User> lowerCase = userRepository.findByUsername("casesensitive");

        // Assert
        assertThat(lowerCase).isEmpty();
    }
}
