package com.medicalrecord.dto.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorBindingModel {

    private Long id; // User ID

    @NotBlank(message = "UIN cannot be blank")
    private String uin;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Specialty cannot be blank")
    private String specialty;

    @NotNull(message = "GP status must be specified")
    private Boolean gp;
}
