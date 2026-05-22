package com.medicalrecord.entity;

import com.medicalrecord.entity.enums.RoleEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class RoleEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private RoleEnum roleName;
}
