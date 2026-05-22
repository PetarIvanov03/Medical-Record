package com.medicalrecord.repository.projection;

public interface DoctorVisitCountProjection {
    Long getDoctorId();
    String getDoctorName();
    Long getVisitCount();
}
