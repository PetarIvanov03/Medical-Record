package com.ivanovp.medical_record.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDTO {

    private Long id;
    private String uin;
    private String name;
    private String specialtyName;
    @JsonProperty("isGp")
    private boolean isGp;
}
