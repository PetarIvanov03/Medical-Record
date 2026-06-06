package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserId(Long userId);

    List<Patient> findByGpId(Long gpId);
}
