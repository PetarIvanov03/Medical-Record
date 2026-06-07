package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByIsGpTrue();

    Optional<Doctor> findByUserId(Long userId);

    boolean existsBySpecialtyId(Long specialtyId);
}
