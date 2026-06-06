package com.ivanovp.medical_record.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateDTO {

    @NotBlank
    private String name;

    @NotNull
    private Long specialtyId;

    private boolean isGp;
}
