package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {

    List<UserResponseDTO> getAllUsers();

    void deleteUser(Long id);
}
