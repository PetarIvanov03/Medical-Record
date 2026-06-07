package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.Specialty;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private SpecialtyRepository specialtyRepository;
    @Autowired private UserRepository userRepository;

    private Doctor gpDoctor;
    private User patientUser;
    private Patient patient;

    @BeforeEach
    void setUp() {
        User doctorUser = new User();
        doctorUser.setUsername("gpDoctor");
        doctorUser.setPassword("password");
        doctorUser.setRole(UserRole.DOCTOR);
        userRepository.save(doctorUser);

        Specialty specialty = new Specialty();
        specialty.setName("General Medicine");
        specialtyRepository.save(specialty);

        gpDoctor = new Doctor();
        gpDoctor.setUin("1234567890");
        gpDoctor.setName("Dr. GP");
        gpDoctor.setGp(true);
        gpDoctor.setSpecialty(specialty);
        gpDoctor.setUser(doctorUser);
        doctorRepository.save(gpDoctor);

        patientUser = new User();
        patientUser.setUsername("patient1");
        patientUser.setPassword("password");
        patientUser.setRole(UserRole.PATIENT);
        userRepository.save(patientUser);

        patient = new Patient();
        patient.setEgn("9876543210");
        patient.setName("Ivan Ivanov");
        patient.setInsured(true);
        patient.setGp(gpDoctor);
        patient.setUser(patientUser);
        patientRepository.save(patient);
    }

    @Test
    void findByUserId_whenPatientLinkedToUser_returnsPatientOptional() {
        // Act
        Optional<Patient> result = patientRepository.findByUserId(patientUser.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Ivan Ivanov");
        assertThat(result.get().getEgn()).isEqualTo("9876543210");
    }

    @Test
    void findByUserId_whenNoMatchingUser_returnsEmpty() {
        // Act
        Optional<Patient> result = patientRepository.findByUserId(9999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByGpId_whenPatientsAssignedToGp_returnsAllPatients() {
        // Arrange - add a second patient with the same GP
        User secondUser = new User();
        secondUser.setUsername("patient2");
        secondUser.setPassword("password");
        secondUser.setRole(UserRole.PATIENT);
        userRepository.save(secondUser);

        Patient secondPatient = new Patient();
        secondPatient.setEgn("1111111111");
        secondPatient.setName("Georgi Petrov");
        secondPatient.setInsured(false);
        secondPatient.setGp(gpDoctor);
        secondPatient.setUser(secondUser);
        patientRepository.save(secondPatient);

        // Act
        List<Patient> result = patientRepository.findByGpId(gpDoctor.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(p -> assertThat(p.getGp().getId()).isEqualTo(gpDoctor.getId()));
    }

    @Test
    void findByGpId_whenNoPatientAssignedToGp_returnsEmptyList() {
        // Arrange
        User doctorUser2 = new User();
        doctorUser2.setUsername("gpDoctor2");
        doctorUser2.setPassword("password");
        doctorUser2.setRole(UserRole.DOCTOR);
        userRepository.save(doctorUser2);

        Specialty specialty2 = new Specialty();
        specialty2.setName("Pediatrics");
        specialtyRepository.save(specialty2);

        Doctor gp2 = new Doctor();
        gp2.setUin("5555555555");
        gp2.setName("Dr. GP2");
        gp2.setGp(true);
        gp2.setSpecialty(specialty2);
        gp2.setUser(doctorUser2);
        doctorRepository.save(gp2);

        // Act
        List<Patient> result = patientRepository.findByGpId(gp2.getId());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void countPatientsByGp_returnsCorrectCountForEachGp() {
        // Act
        List<Object[]> result = patientRepository.countPatientsByGp();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)[0]).isEqualTo("Dr. GP");
        assertThat(((Number) result.get(0)[1]).longValue()).isEqualTo(1L);
    }
}
