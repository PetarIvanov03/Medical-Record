package com.ivanovp.medical_record.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorCreateDTO {

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String uin;

    @NotBlank
    private String name;

    @NotNull
    private Long specialtyId;

    private boolean isGp;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6)
    private String password;
}
