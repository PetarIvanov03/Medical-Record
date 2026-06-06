package com.ivanovp.medical_record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SickLeaveResponseDTO {

    private Long id;
    private LocalDate startDate;
    private int durationDays;
    private Long examinationId;
    private String patientName;
    private String doctorName;
}
