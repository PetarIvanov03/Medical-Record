package com.medicalrecord.dto.view;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportItemViewModel {
    private String label;
    private String value;

    public ReportItemViewModel(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
