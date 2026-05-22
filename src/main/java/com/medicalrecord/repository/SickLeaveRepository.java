package com.medicalrecord.repository;

import com.medicalrecord.entity.SickLeave;
import com.medicalrecord.repository.projection.DoctorSickLeaveCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    // 10. The month with the highest number of issued sick leaves.
    @Query(value = "SELECT MONTH(start_date) FROM sick_leaves GROUP BY MONTH(start_date) ORDER BY COUNT(id) DESC LIMIT 1", nativeQuery = true)
    Optional<Integer> findMonthWithMostSickLeaves();

    // 11. The doctor(s) who issued the most sick leaves.
    // Fetch all counts grouped by doctor, ordered descending. Service layer will extract the top ties if necessary.
    @Query("SELECT d.id AS doctorId, d.name AS doctorName, COUNT(s) AS sickLeaveCount " +
           "FROM SickLeave s JOIN s.exam e JOIN e.doctor d " +
           "GROUP BY d.id, d.name " +
           "ORDER BY sickLeaveCount DESC")
    List<DoctorSickLeaveCountProjection> findDoctorsWithMostSickLeavesDesc();
}
