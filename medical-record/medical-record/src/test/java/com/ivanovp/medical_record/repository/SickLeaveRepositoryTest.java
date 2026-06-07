package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Examination;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.SickLeave;
import com.ivanovp.medical_record.entity.Specialty;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SickLeaveRepositoryTest {

    @Autowired private SickLeaveRepository sickLeaveRepository;
    @Autowired private ExaminationRepository examinationRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private SpecialtyRepository specialtyRepository;
    @Autowired private UserRepository userRepository;

    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        User doctorUser = new User();
        doctorUser.setUsername("doctor");
        doctorUser.setPassword("password");
        doctorUser.setRole(UserRole.DOCTOR);
        userRepository.save(doctorUser);

        User patientUser = new User();
        patientUser.setUsername("patient");
        patientUser.setPassword("password");
        patientUser.setRole(UserRole.PATIENT);
        userRepository.save(patientUser);

        Specialty specialty = new Specialty();
        specialty.setName("General Medicine");
        specialtyRepository.save(specialty);

        doctor = new Doctor();
        doctor.setUin("1234567890");
        doctor.setName("Dr. Ivanov");
        doctor.setGp(true);
        doctor.setSpecialty(specialty);
        doctor.setUser(doctorUser);
        doctorRepository.save(doctor);

        patient = new Patient();
        patient.setEgn("9876543210");
        patient.setName("Patient Ivanov");
        patient.setInsured(true);
        patient.setGp(doctor);
        patient.setUser(patientUser);
        patientRepository.save(patient);
    }

    private Examination saveExamination() {
        Examination exam = new Examination();
        exam.setExamDate(LocalDateTime.now());
        exam.setTreatment("Treatment");
        exam.setPrice(new BigDecimal("100.00"));
        exam.setPaidByNzok(true);
        exam.setDoctor(doctor);
        exam.setPatient(patient);
        return examinationRepository.save(exam);
    }

    @Test
    void findByExaminationId_whenSickLeaveExists_returnsSickLeaveOptional() {
        // Arrange
        Examination exam = saveExamination();
        SickLeave leave = new SickLeave();
        leave.setStartDate(LocalDate.of(2024, 3, 15));
        leave.setDurationDays(7);
        leave.setExamination(exam);
        sickLeaveRepository.save(leave);

        // Act
        Optional<SickLeave> result = sickLeaveRepository.findByExaminationId(exam.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getDurationDays()).isEqualTo(7);
    }

    @Test
    void findByExaminationId_whenNoSickLeaveForExamination_returnsEmpty() {
        // Arrange
        Examination exam = saveExamination();

        // Act
        Optional<SickLeave> result = sickLeaveRepository.findByExaminationId(exam.getId());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void existsByExaminationId_whenSickLeaveExists_returnsTrue() {
        // Arrange
        Examination exam = saveExamination();
        SickLeave leave = new SickLeave();
        leave.setStartDate(LocalDate.now());
        leave.setDurationDays(5);
        leave.setExamination(exam);
        sickLeaveRepository.save(leave);

        // Act
        boolean result = sickLeaveRepository.existsByExaminationId(exam.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void existsByExaminationId_whenNoSickLeave_returnsFalse() {
        // Arrange
        Examination exam = saveExamination();

        // Act
        boolean result = sickLeaveRepository.existsByExaminationId(exam.getId());

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void findDoctorsWithMostSickLeaves_returnsCorrectRanking() {
        // Arrange - create 2 sick leaves for this doctor
        Examination exam1 = saveExamination();
        SickLeave leave1 = new SickLeave();
        leave1.setStartDate(LocalDate.of(2024, 1, 10));
        leave1.setDurationDays(3);
        leave1.setExamination(exam1);
        sickLeaveRepository.save(leave1);

        Examination exam2 = saveExamination();
        SickLeave leave2 = new SickLeave();
        leave2.setStartDate(LocalDate.of(2024, 2, 10));
        leave2.setDurationDays(5);
        leave2.setExamination(exam2);
        sickLeaveRepository.save(leave2);

        // Act
        List<Object[]> result = sickLeaveRepository.findDoctorsWithMostSickLeaves();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)[0]).isEqualTo("Dr. Ivanov");
        assertThat(((Number) result.get(0)[1]).longValue()).isEqualTo(2L);
    }
}
