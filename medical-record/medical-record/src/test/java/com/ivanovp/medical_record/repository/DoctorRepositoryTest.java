package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Doctor;
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
class DoctorRepositoryTest {

    @Autowired private DoctorRepository doctorRepository;
    @Autowired private SpecialtyRepository specialtyRepository;
    @Autowired private UserRepository userRepository;

    private Specialty specialty;
    private User gpUser;
    private User specialistUser;
    private Doctor gpDoctor;
    private Doctor specialistDoctor;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setName("Cardiology");
        specialtyRepository.save(specialty);

        gpUser = new User();
        gpUser.setUsername("gpDoctor");
        gpUser.setPassword("password");
        gpUser.setRole(UserRole.DOCTOR);
        userRepository.save(gpUser);

        specialistUser = new User();
        specialistUser.setUsername("specialist");
        specialistUser.setPassword("password");
        specialistUser.setRole(UserRole.DOCTOR);
        userRepository.save(specialistUser);

        gpDoctor = new Doctor();
        gpDoctor.setUin("1111111111");
        gpDoctor.setName("Dr. GP");
        gpDoctor.setGp(true);
        gpDoctor.setSpecialty(specialty);
        gpDoctor.setUser(gpUser);
        doctorRepository.save(gpDoctor);

        specialistDoctor = new Doctor();
        specialistDoctor.setUin("2222222222");
        specialistDoctor.setName("Dr. Specialist");
        specialistDoctor.setGp(false);
        specialistDoctor.setSpecialty(specialty);
        specialistDoctor.setUser(specialistUser);
        doctorRepository.save(specialistDoctor);
    }

    @Test
    void findByIsGpTrue_returnsOnlyGpDoctors() {
        // Act
        List<Doctor> result = doctorRepository.findByIsGpTrue();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).allSatisfy(doctor -> assertThat(doctor.isGp()).isTrue());
        assertThat(result.get(0).getName()).isEqualTo("Dr. GP");
    }

    @Test
    void findByUserId_whenDoctorLinkedToUser_returnsDoctorOptional() {
        // Act
        Optional<Doctor> result = doctorRepository.findByUserId(gpUser.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Dr. GP");
    }

    @Test
    void findByUserId_whenNoMatchingUser_returnsEmpty() {
        // Act
        Optional<Doctor> result = doctorRepository.findByUserId(9999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void existsBySpecialtyId_whenDoctorHasSpecialty_returnsTrue() {
        // Act
        boolean result = doctorRepository.existsBySpecialtyId(specialty.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void existsBySpecialtyId_whenNoDoctoHasSpecialty_returnsFalse() {
        // Arrange
        Specialty unusedSpecialty = new Specialty();
        unusedSpecialty.setName("Unused");
        specialtyRepository.save(unusedSpecialty);

        // Act
        boolean result = doctorRepository.existsBySpecialtyId(unusedSpecialty.getId());

        // Assert
        assertThat(result).isFalse();
    }
}
