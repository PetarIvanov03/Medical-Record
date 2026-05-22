package com.medicalrecord.dto.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExamBindingModel {

    private Long id;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Patient must be selected")
    private Long patientId;

    @NotBlank(message = "Diagnosis cannot be blank")
    private String diagnosis;

    private String treatment;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
}
