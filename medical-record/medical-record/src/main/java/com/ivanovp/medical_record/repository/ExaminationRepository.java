package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Examination;
import com.ivanovp.medical_record.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByDoctorId(Long doctorId);

    List<Examination> findByPatientId(Long patientId);

    List<Examination> findByExamDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT DISTINCT e.patient FROM Examination e WHERE e.diagnosis.id = :diagnosisId")
    List<Patient> findPatientsByDiagnosisId(@Param("diagnosisId") Long diagnosisId);

    @Query("SELECT e.diagnosis, COUNT(e) FROM Examination e WHERE e.diagnosis IS NOT NULL GROUP BY e.diagnosis ORDER BY COUNT(e) DESC LIMIT 1")
    List<Object[]> findMostCommonDiagnosisWithCount();

    @Query("SELECT SUM(e.price) FROM Examination e WHERE e.paidByNzok = false")
    BigDecimal findTotalPatientRevenue();

    @Query("SELECT e.doctor.name, SUM(e.price) FROM Examination e WHERE e.paidByNzok = false GROUP BY e.doctor.name")
    List<Object[]> findRevenueByDoctor();

    @Query("SELECT e.doctor.name, COUNT(e) FROM Examination e GROUP BY e.doctor.name")
    List<Object[]> countVisitsPerDoctor();

    List<Examination> findByDiagnosisId(Long diagnosisId);
}
