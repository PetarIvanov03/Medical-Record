package com.ivanovp.medical_record.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SickLeaveRequestDTO {

    @NotNull(message = "Examination ID is required")
    private Long examinationId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Duration days is required")
    @Min(value = 1, message = "Duration days must be at least 1")
    private int durationDays;
}
