package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    boolean existsByCode(String code);
}
