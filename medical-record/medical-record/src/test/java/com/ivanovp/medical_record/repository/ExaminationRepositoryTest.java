package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Diagnosis;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Examination;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.Specialty;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExaminationRepositoryTest {

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        // Create user for doctor
        User doctorUser = new User();
        doctorUser.setUsername("doctor");
        doctorUser.setPassword("password");
        doctorUser.setRole(UserRole.DOCTOR);
        userRepository.save(doctorUser);

        // Create user for patient
        User patientUser = new User();
        patientUser.setUsername("patient");
        patientUser.setPassword("password");
        patientUser.setRole(UserRole.PATIENT);
        userRepository.save(patientUser);

        // Create specialty
        Specialty specialty = new Specialty();
        specialty.setName("Cardiology");
        specialtyRepository.save(specialty);

        // Create doctor
        Doctor doctor = new Doctor();
        doctor.setUin("1234567890");
        doctor.setName("Dr. Ivanov");
        doctor.setGp(false);
        doctor.setSpecialty(specialty);
        doctor.setUser(doctorUser);
        doctorRepository.save(doctor);

        // Create patient
        Patient patient = new Patient();
        patient.setEgn("9876543210");
        patient.setName("Patient Ivanov");
        patient.setInsured(true);
        patient.setGp(doctor);
        patient.setUser(patientUser);
        patientRepository.save(patient);
    }

    @Test
    void findByDoctorId_returnsOnlyDoctorExaminations() {
        Doctor doctor = doctorRepository.findAll().get(0);

        Examination exam1 = new Examination();
        exam1.setExamDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        exam1.setTreatment("Check-up");
        exam1.setPrice(new BigDecimal("100"));
        exam1.setPaidByNzok(true);
        exam1.setDoctor(doctor);
        exam1.setPatient(patientRepository.findAll().get(0));
        examinationRepository.save(exam1);

        Examination exam2 = new Examination();
        exam2.setExamDate(LocalDateTime.of(2024, 2, 1, 10, 0));
        exam2.setTreatment("Consultation");
        exam2.setPrice(new BigDecimal("150"));
        exam2.setPaidByNzok(false);
        exam2.setDoctor(doctor);
        exam2.setPatient(patientRepository.findAll().get(0));
        examinationRepository.save(exam2);

        List<Examination> result = examinationRepository.findByDoctorId(doctor.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(exam -> assertThat(exam.getDoctor().getId()).isEqualTo(doctor.getId()));
    }

    @Test
    void findByPatientId_returnsOnlyPatientExaminations() {
        Doctor doctor = doctorRepository.findAll().get(0);
        Patient patient = patientRepository.findAll().get(0);

        Examination exam1 = new Examination();
        exam1.setExamDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        exam1.setTreatment("Check-up");
        exam1.setPrice(new BigDecimal("100"));
        exam1.setPaidByNzok(true);
        exam1.setDoctor(doctor);
        exam1.setPatient(patient);
        examinationRepository.save(exam1);

        Examination exam2 = new Examination();
        exam2.setExamDate(LocalDateTime.of(2024, 2, 1, 10, 0));
        exam2.setTreatment("Consultation");
        exam2.setPrice(new BigDecimal("150"));
        exam2.setPaidByNzok(false);
        exam2.setDoctor(doctor);
        exam2.setPatient(patient);
        examinationRepository.save(exam2);

        List<Examination> result = examinationRepository.findByPatientId(patient.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(exam -> assertThat(exam.getPatient().getId()).isEqualTo(patient.getId()));
    }

    @Test
    void findByExamDateBetween_returnsExaminationsInRange() {
        Doctor doctor = doctorRepository.findAll().get(0);
        Patient patient = patientRepository.findAll().get(0);

        Examination examInRange1 = new Examination();
        examInRange1.setExamDate(LocalDateTime.of(2024, 3, 15, 10, 0));
        examInRange1.setTreatment("Check-up");
        examInRange1.setPrice(new BigDecimal("100"));
        examInRange1.setPaidByNzok(true);
        examInRange1.setDoctor(doctor);
        examInRange1.setPatient(patient);
        examinationRepository.save(examInRange1);

        Examination examInRange2 = new Examination();
        examInRange2.setExamDate(LocalDateTime.of(2024, 3, 20, 14, 0));
        examInRange2.setTreatment("Consultation");
        examInRange2.setPrice(new BigDecimal("150"));
        examInRange2.setPaidByNzok(false);
        examInRange2.setDoctor(doctor);
        examInRange2.setPatient(patient);
        examinationRepository.save(examInRange2);

        Examination examOutRange = new Examination();
        examOutRange.setExamDate(LocalDateTime.of(2024, 4, 10, 10, 0));
        examOutRange.setTreatment("Follow-up");
        examOutRange.setPrice(new BigDecimal("200"));
        examOutRange.setPaidByNzok(true);
        examOutRange.setDoctor(doctor);
        examOutRange.setPatient(patient);
        examinationRepository.save(examOutRange);

        LocalDateTime from = LocalDateTime.of(2024, 3, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 3, 31, 23, 59);

        List<Examination> result = examinationRepository.findByExamDateBetween(from, to);

        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(exam -> {
            assertThat(exam.getExamDate()).isAfterOrEqualTo(from);
            assertThat(exam.getExamDate()).isBeforeOrEqualTo(to);
        });
    }
}
