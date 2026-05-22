package com.medicalrecord.repository;

import com.medicalrecord.entity.Doctor;
import com.medicalrecord.repository.projection.DoctorPatientCountProjection;
import com.medicalrecord.repository.projection.DoctorVisitCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 6. Number of patients registered to each GP
    @Query("SELECT d.id AS doctorId, d.name AS doctorName, COUNT(p) AS patientCount " +
           "FROM Doctor d LEFT JOIN d.registeredPatients p " +
           "WHERE d.gp = true " +
           "GROUP BY d.id, d.name")
    List<DoctorPatientCountProjection> countPatientsPerGp();

    // 7. Number of visits/exams per doctor
    @Query("SELECT d.id AS doctorId, d.name AS doctorName, COUNT(e) AS visitCount " +
           "FROM Doctor d LEFT JOIN d.conductedExams e " +
           "GROUP BY d.id, d.name")
    List<DoctorVisitCountProjection> countVisitsPerDoctor();
}
