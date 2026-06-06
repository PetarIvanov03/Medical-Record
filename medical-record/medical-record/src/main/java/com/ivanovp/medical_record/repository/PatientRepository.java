package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserId(Long userId);

    List<Patient> findByGpId(Long gpId);

    @Query("SELECT p.gp.name, COUNT(p) FROM Patient p WHERE p.gp IS NOT NULL GROUP BY p.gp.name")
    List<Object[]> countPatientsByGp();
}

