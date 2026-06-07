package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.ExaminationRequestDTO;
import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Examination;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.repository.DiagnosisRepository;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.PatientRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExaminationServiceTest {

    @Mock
    private ExaminationRepository examinationRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExaminationServiceImpl examinationService;

    @Test
    void createExamination_whenPatientInsured_setPaidByNzokTrue() {
        // Arrange
        String username = "doctor1";
        Long patientId = 1L;

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUser(user);

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setInsured(true);

        ExaminationRequestDTO dto = new ExaminationRequestDTO(patientId, null, "Checkup", new BigDecimal("100.00"));

        Examination savedExamination = new Examination();
        savedExamination.setId(1L);
        savedExamination.setPaidByNzok(true);
        savedExamination.setPatient(patient);
        savedExamination.setDoctor(doctor);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(user.getId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(examinationRepository.save(any(Examination.class))).thenReturn(savedExamination);

        // Act
        ExaminationResponseDTO result = examinationService.createExamination(dto, username);

        // Assert
        assertThat(result.isPaidByNzok()).isTrue();
    }

    @Test
    void createExamination_whenPatientNotInsured_setPaidByNzokFalse() {
        // Arrange
        String username = "doctor1";
        Long patientId = 2L;

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUser(user);

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setInsured(false);

        ExaminationRequestDTO dto = new ExaminationRequestDTO(patientId, null, "Checkup", new BigDecimal("100.00"));

        Examination savedExamination = new Examination();
        savedExamination.setId(2L);
        savedExamination.setPaidByNzok(false);
        savedExamination.setPatient(patient);
        savedExamination.setDoctor(doctor);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(user.getId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(examinationRepository.save(any(Examination.class))).thenReturn(savedExamination);

        // Act
        ExaminationResponseDTO result = examinationService.createExamination(dto, username);

        // Assert
        assertThat(result.isPaidByNzok()).isFalse();
    }

    @Test
    void updateExamination_whenNotOwner_throwsAccessDeniedException() {
        // Arrange
        Long examinationId = 1L;
        String callerUsername = "differentDoctor";

        User doctorUser = new User();
        doctorUser.setUsername("ownerDoctor");

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUser(doctorUser);

        Examination examination = new Examination();
        examination.setId(examinationId);
        examination.setDoctor(doctor);

        ExaminationRequestDTO dto = new ExaminationRequestDTO(1L, null, "Updated treatment", new BigDecimal("150.00"));

        when(examinationRepository.findById(examinationId)).thenReturn(Optional.of(examination));

        // Act & Assert
        assertThatThrownBy(() -> examinationService.updateExamination(examinationId, dto, callerUsername))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are not authorized to update this examination");
    }
}

