package com.medicalrecord.dto.view;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorViewModel {
    private Long id;
    private String uin;
    private String name;
    private String specialty;
    private boolean gp;
}
