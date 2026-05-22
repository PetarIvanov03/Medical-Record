package com.medicalrecord.repository.projection;

public interface DoctorSickLeaveCountProjection {
    Long getDoctorId();
    String getDoctorName();
    Long getSickLeaveCount();
}
