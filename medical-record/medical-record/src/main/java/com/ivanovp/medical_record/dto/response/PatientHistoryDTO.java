package com.ivanovp.medical_record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientHistoryDTO {

    private Long id;
    private String name;
    private String egn;
    private List<ExaminationResponseDTO> examinations;
}
