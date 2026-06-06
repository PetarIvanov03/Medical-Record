package com.ivanovp.medical_record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationResponseDTO {

    private Long id;
    private LocalDateTime examDate;
    private String doctorName;
    private String patientName;
    private String diagnosisCode;
    private String diagnosisName;
    private String treatment;
    private BigDecimal price;
    private boolean paidByNzok;
}
