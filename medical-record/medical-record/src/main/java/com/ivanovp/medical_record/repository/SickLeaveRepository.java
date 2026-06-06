package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {
    Optional<SickLeave> findByExaminationId(Long examinationId);

    boolean existsByExaminationId(Long examinationId);

    @Query("SELECT MONTH(sl.startDate) FROM SickLeave sl GROUP BY MONTH(sl.startDate) ORDER BY COUNT(sl) DESC LIMIT 1")
    Integer findPeakSickLeaveMonth();

    @Query("SELECT sl.examination.doctor.name, COUNT(sl) FROM SickLeave sl GROUP BY sl.examination.doctor.name ORDER BY COUNT(sl) DESC")
    List<Object[]> findDoctorsWithMostSickLeaves();
}
