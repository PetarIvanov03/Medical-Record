package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.UserLoginDTO;
import com.ivanovp.medical_record.dto.request.UserRegisterDTO;
import com.ivanovp.medical_record.dto.response.JwtAuthResponseDTO;

public interface AuthService {

    JwtAuthResponseDTO register(UserRegisterDTO dto);

    JwtAuthResponseDTO login(UserLoginDTO dto);
}
