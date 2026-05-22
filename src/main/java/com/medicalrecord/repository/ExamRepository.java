package com.medicalrecord.repository;

import com.medicalrecord.entity.Exam;
import com.medicalrecord.repository.projection.DoctorIncomeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // 2. Most common diagnosis across the system (Native query as JPQL limit is dialect dependent without Pageable)
    @Query(value = "SELECT diagnosis FROM exams GROUP BY diagnosis ORDER BY COUNT(id) DESC LIMIT 1", nativeQuery = true)
    Optional<String> findMostCommonDiagnosis();

    // 4. Total monetary value of exams paid by patients out-of-pocket
    // Business Rule: Uninsured patients pay out-of-pocket. Insured ones are covered by NZOK.
    @Query("SELECT SUM(e.price) FROM Exam e JOIN e.patient p WHERE p.hasInsurance = false")
    Optional<BigDecimal> calculateTotalOutofPocket();

    // 5. Monetary value of exams paid by patients, grouped by the performing doctor
    @Query("SELECT d.id AS doctorId, d.name AS doctorName, SUM(e.price) AS totalIncome " +
           "FROM Exam e JOIN e.doctor d JOIN e.patient p " +
           "WHERE p.hasInsurance = false " +
           "GROUP BY d.id, d.name")
    List<DoctorIncomeProjection> calculateTotalOutofPocketByDoctor();

    // 8. Complete visit history of a specific patient
    List<Exam> findAllByPatientIdOrderByDateDesc(Long patientId);

    // 9. Filtered exams by doctor and/or a specific time period
    @Query("SELECT e FROM Exam e WHERE e.doctor.id = :doctorId AND e.date >= :startDate AND e.date <= :endDate ORDER BY e.date DESC")
    List<Exam> findAllByDoctorIdAndDateBetween(@Param("doctorId") Long doctorId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
