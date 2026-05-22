package com.medicalrecord.dto.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientBindingModel {

    private Long id; // User ID

    @NotBlank(message = "EGN cannot be blank")
    private String egn;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Insurance status must be specified")
    private Boolean hasInsurance;

    @NotNull(message = "GP must be selected")
    private Long gpId;
}
