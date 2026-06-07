package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.DoctorCreateDTO;
import com.ivanovp.medical_record.dto.request.DoctorUpdateDTO;
import com.ivanovp.medical_record.dto.response.DoctorResponseDTO;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Specialty;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.PatientRepository;
import com.ivanovp.medical_record.repository.SpecialtyRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private SpecialtyRepository specialtyRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ExaminationRepository examinationRepository;
    @Mock private PatientRepository patientRepository;
    @InjectMocks private DoctorServiceImpl doctorService;

    @Test
    void getAllDoctors_returnsListOfDoctorResponseDTOs() {
        // Arrange
        Doctor doctor = buildDoctor(1L, "1234567890", "Dr. Smith", false);
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        // Act
        List<DoctorResponseDTO> result = doctorService.getAllDoctors();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Dr. Smith");
    }

    @Test
    void getDoctorById_whenDoctorExists_returnsDoctorResponseDTO() {
        // Arrange
        Doctor doctor = buildDoctor(1L, "1234567890", "Dr. Smith", false);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // Act
        DoctorResponseDTO result = doctorService.getDoctorById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUin()).isEqualTo("1234567890");
    }

    @Test
    void getDoctorById_whenDoctorNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.getDoctorById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createDoctor_whenUsernameAvailableAndSpecialtyExists_returnsDoctorResponseDTO() {
        // Arrange
        DoctorCreateDTO dto = new DoctorCreateDTO("1234567890", "Dr. Smith", 1L, false, "drsmith", "password123");
        Specialty specialty = buildSpecialty(1L, "Cardiology");
        User savedUser = buildUser(1L, "drsmith");
        Doctor savedDoctor = buildDoctor(1L, "1234567890", "Dr. Smith", false);
        savedDoctor.setSpecialty(specialty);

        when(userRepository.findByUsername("drsmith")).thenReturn(Optional.empty());
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        // Act
        DoctorResponseDTO result = doctorService.createDoctor(dto);

        // Assert
        assertThat(result.getName()).isEqualTo("Dr. Smith");
        assertThat(result.getSpecialtyName()).isEqualTo("Cardiology");
    }

    @Test
    void createDoctor_whenUsernameAlreadyTaken_throwsIllegalArgumentException() {
        // Arrange
        DoctorCreateDTO dto = new DoctorCreateDTO("1234567890", "Dr. Smith", 1L, false, "existing", "password123");
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThatThrownBy(() -> doctorService.createDoctor(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existing");
    }

    @Test
    void createDoctor_whenSpecialtyNotFound_throwsResourceNotFoundException() {
        // Arrange
        DoctorCreateDTO dto = new DoctorCreateDTO("1234567890", "Dr. Smith", 99L, false, "drsmith", "password123");
        when(userRepository.findByUsername("drsmith")).thenReturn(Optional.empty());
        when(specialtyRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.createDoctor(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateDoctor_whenDoctorExists_returnsUpdatedDoctorResponseDTO() {
        // Arrange
        DoctorUpdateDTO dto = new DoctorUpdateDTO("Dr. Updated", 2L, true);
        Doctor existingDoctor = buildDoctor(1L, "1234567890", "Dr. Smith", false);
        Specialty newSpecialty = buildSpecialty(2L, "Neurology");
        Doctor updatedDoctor = buildDoctor(1L, "1234567890", "Dr. Updated", true);
        updatedDoctor.setSpecialty(newSpecialty);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existingDoctor));
        when(specialtyRepository.findById(2L)).thenReturn(Optional.of(newSpecialty));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);

        // Act
        DoctorResponseDTO result = doctorService.updateDoctor(1L, dto);

        // Assert
        assertThat(result.getName()).isEqualTo("Dr. Updated");
        assertThat(result.isGp()).isTrue();
    }

    @Test
    void updateDoctor_whenDoctorNotFound_throwsResourceNotFoundException() {
        // Arrange
        DoctorUpdateDTO dto = new DoctorUpdateDTO("Dr. Updated", 1L, false);
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.updateDoctor(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteDoctor_whenDoctorHasExaminations_throwsIllegalStateException() {
        // Arrange
        Doctor doctor = buildDoctor(1L, "1234567890", "Dr. Smith", false);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(examinationRepository.findByDoctorId(1L)).thenReturn(List.of(new com.ivanovp.medical_record.entity.Examination()));

        // Act & Assert
        assertThatThrownBy(() -> doctorService.deleteDoctor(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("examinations");
    }

    @Test
    void deleteDoctor_whenDoctorExistsWithNoExaminations_deletesSuccessfully() {
        // Arrange
        User user = buildUser(1L, "drsmith");
        Doctor doctor = buildDoctor(1L, "1234567890", "Dr. Smith", false);
        doctor.setUser(user);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(examinationRepository.findByDoctorId(1L)).thenReturn(List.of());
        when(patientRepository.findByGpId(1L)).thenReturn(List.of());

        // Act
        doctorService.deleteDoctor(1L);

        // Assert
        verify(doctorRepository).delete(doctor);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteDoctor_whenDoctorNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.deleteDoctor(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    private Doctor buildDoctor(Long id, String uin, String name, boolean isGp) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setUin(uin);
        doctor.setName(name);
        doctor.setGp(isGp);
        return doctor;
    }

    private Specialty buildSpecialty(Long id, String name) {
        Specialty specialty = new Specialty();
        specialty.setId(id);
        specialty.setName(name);
        return specialty;
    }

    private User buildUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(UserRole.DOCTOR);
        return user;
    }
}
