package com.medicalrecord.service.impl;

import com.medicalrecord.dto.binding.UserRegisterBindingModel;
import com.medicalrecord.entity.RoleEntity;
import com.medicalrecord.entity.UserEntity;
import com.medicalrecord.entity.enums.RoleEnum;
import com.medicalrecord.repository.RoleRepository;
import com.medicalrecord.repository.UserRepository;
import com.medicalrecord.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@org.springframework.context.annotation.DependsOn("roleServiceImpl")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(UserRegisterBindingModel bindingModel) {
        if (userRepository.findByUsername(bindingModel.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(bindingModel.getUsername());
        user.setPassword(passwordEncoder.encode(bindingModel.getPassword()));

        RoleEnum roleEnum = RoleEnum.valueOf(bindingModel.getRole().toUpperCase());
        RoleEntity role = roleRepository.findByRoleName(roleEnum)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PostConstruct
    @Override
    public void initAdmin() {
        if (userRepository.count() == 0) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));

            RoleEntity adminRole = roleRepository.findByRoleName(RoleEnum.ADMIN)
                    .orElseThrow();
            admin.getRoles().add(adminRole);

            userRepository.save(admin);
        }
    }
}
