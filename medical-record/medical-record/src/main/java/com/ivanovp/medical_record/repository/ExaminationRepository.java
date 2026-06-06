package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByDoctorId(Long doctorId);

    List<Examination> findByPatientId(Long patientId);

    List<Examination> findByExamDateBetween(LocalDateTime from, LocalDateTime to);
}
