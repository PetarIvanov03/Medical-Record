package com.ivanovp.medical_record.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {

    private Long id;
    private String username;
    private String name;
    private String egn;
    @JsonProperty("isInsured")
    private boolean isInsured;
    private String gpName;
}
