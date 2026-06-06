package com.ivanovp.medical_record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {

    private Long id;
    private String name;
    private String egn;
    private boolean isInsured;
    private String gpName;
}
