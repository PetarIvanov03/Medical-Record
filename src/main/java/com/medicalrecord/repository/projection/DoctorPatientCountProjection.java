package com.medicalrecord.repository.projection;

public interface DoctorPatientCountProjection {
    Long getDoctorId();
    String getDoctorName();
    Long getPatientCount();
}
