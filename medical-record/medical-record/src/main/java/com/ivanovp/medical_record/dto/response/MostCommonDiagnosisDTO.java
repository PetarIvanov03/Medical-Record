package com.ivanovp.medical_record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MostCommonDiagnosisDTO {

    private String code;
    private String name;
    private Long count;
}
