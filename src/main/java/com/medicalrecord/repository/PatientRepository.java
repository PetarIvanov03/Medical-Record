package com.medicalrecord.repository;

import com.medicalrecord.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // 1. List of patients with a given diagnosis
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.exams e WHERE e.diagnosis = :diagnosis")
    List<Patient> findAllByExamsDiagnosis(@Param("diagnosis") String diagnosis);

    // 3. List of patients registered to a given GP
    List<Patient> findAllByGpId(Long gpId);
}
