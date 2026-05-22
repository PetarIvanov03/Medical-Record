package com.medicalrecord.service.impl;

import com.medicalrecord.entity.RoleEntity;
import com.medicalrecord.entity.enums.RoleEnum;
import com.medicalrecord.repository.RoleRepository;
import com.medicalrecord.service.RoleService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@org.springframework.core.annotation.Order(1)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    @Override
    public void initRoles() {
        if (roleRepository.count() == 0) {
            List<RoleEntity> roles = Arrays.stream(RoleEnum.values())
                    .map(roleEnum -> {
                        RoleEntity role = new RoleEntity();
                        role.setRoleName(roleEnum);
                        return role;
                    })
                    .toList();
            roleRepository.saveAll(roles);
        }
    }
}
