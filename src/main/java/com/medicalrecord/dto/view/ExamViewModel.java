package com.medicalrecord.dto.view;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExamViewModel {
    private Long id;
    private LocalDate date;
    private DoctorViewModel doctor;
    private PatientViewModel patient;
    private String diagnosis;
    private String treatment;
    private BigDecimal price;
}
