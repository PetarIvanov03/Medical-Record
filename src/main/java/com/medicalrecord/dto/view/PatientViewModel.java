package com.medicalrecord.dto.view;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientViewModel {
    private Long id;
    private String egn;
    private String name;
    private boolean hasInsurance;
    private DoctorViewModel gp;
}
