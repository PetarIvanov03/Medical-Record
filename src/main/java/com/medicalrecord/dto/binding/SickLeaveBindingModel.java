package com.medicalrecord.dto.binding;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SickLeaveBindingModel {

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "Number of days cannot be null")
    @Positive(message = "Number of days must be positive")
    private Integer numDays;

    @NotNull(message = "Exam must be provided")
    private Long examId;
}
