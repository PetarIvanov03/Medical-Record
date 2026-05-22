package com.medicalrecord.repository.projection;

import java.math.BigDecimal;

public interface DoctorIncomeProjection {
    Long getDoctorId();
    String getDoctorName();
    BigDecimal getTotalIncome();
}
