package com.ivanovp.medical_record.service.impl;

import com.ivanovp.medical_record.dto.response.UserResponseDTO;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != UserRole.ADMIN)
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
