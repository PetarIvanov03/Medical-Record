package com.medicalrecord.dto.view;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SickLeaveViewModel {
    private Long id;
    private LocalDate startDate;
    private int numDays;
    private ExamViewModel exam;
}
