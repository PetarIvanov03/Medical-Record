package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {
    Optional<SickLeave> findByExaminationId(Long examinationId);

    boolean existsByExaminationId(Long examinationId);
}
