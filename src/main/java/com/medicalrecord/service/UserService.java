package com.medicalrecord.service;

import com.medicalrecord.dto.binding.UserRegisterBindingModel;
import com.medicalrecord.entity.UserEntity;

public interface UserService {
    void registerUser(UserRegisterBindingModel userRegisterBindingModel);
    UserEntity findByUsername(String username);
    void initAdmin();
}
