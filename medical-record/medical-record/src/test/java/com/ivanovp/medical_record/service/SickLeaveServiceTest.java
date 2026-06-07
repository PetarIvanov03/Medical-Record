package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.SickLeaveRequestDTO;
import com.ivanovp.medical_record.dto.response.SickLeaveResponseDTO;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Examination;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.SickLeave;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.SickLeaveRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.impl.SickLeaveServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SickLeaveServiceTest {

    @Mock private SickLeaveRepository sickLeaveRepository;
    @Mock private ExaminationRepository examinationRepository;
    @Mock private UserRepository userRepository;
    @Mock private DoctorRepository doctorRepository;
    @InjectMocks private SickLeaveServiceImpl sickLeaveService;

    @Test
    void getSickLeaveById_whenExists_returnsSickLeaveResponseDTO() {
        // Arrange
        SickLeave sickLeave = buildSickLeave(1L, buildExamination(1L, buildDoctor(1L, "owner"), buildPatient(1L)));
        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));

        // Act
        SickLeaveResponseDTO result = sickLeaveService.getSickLeaveById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDurationDays()).isEqualTo(7);
    }

    @Test
    void getSickLeaveById_whenNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(sickLeaveRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sickLeaveService.getSickLeaveById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createSickLeave_whenOwnerAndNoExistingSickLeave_returnsSickLeaveResponseDTO() {
        // Arrange
        String username = "doctor1";
        User user = buildUser(1L, username);
        Doctor doctor = buildDoctor(1L, username);
        doctor.setUser(user);
        Examination examination = buildExamination(1L, doctor, buildPatient(1L));

        SickLeaveRequestDTO dto = new SickLeaveRequestDTO(1L, LocalDate.now(), 7);

        SickLeave savedLeave = buildSickLeave(1L, examination);

        when(sickLeaveRepository.existsByExaminationId(1L)).thenReturn(false);
        when(examinationRepository.findById(1L)).thenReturn(Optional.of(examination));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(doctor));
        when(sickLeaveRepository.save(any(SickLeave.class))).thenReturn(savedLeave);

        // Act
        SickLeaveResponseDTO result = sickLeaveService.createSickLeave(dto, username);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDurationDays()).isEqualTo(7);
    }

    @Test
    void createSickLeave_whenSickLeaveAlreadyExistsForExamination_throwsIllegalArgumentException() {
        // Arrange
        SickLeaveRequestDTO dto = new SickLeaveRequestDTO(1L, LocalDate.now(), 7);
        when(sickLeaveRepository.existsByExaminationId(1L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> sickLeaveService.createSickLeave(dto, "doctor1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createSickLeave_whenDoctorIsNotExaminationOwner_throwsAccessDeniedException() {
        // Arrange
        String callerUsername = "differentDoctor";
        User callerUser = buildUser(2L, callerUsername);
        Doctor callerDoctor = buildDoctor(2L, callerUsername);
        callerDoctor.setUser(callerUser);

        Doctor ownerDoctor = buildDoctor(1L, "ownerDoctor");
        Examination examination = buildExamination(1L, ownerDoctor, buildPatient(1L));

        SickLeaveRequestDTO dto = new SickLeaveRequestDTO(1L, LocalDate.now(), 7);

        when(sickLeaveRepository.existsByExaminationId(1L)).thenReturn(false);
        when(examinationRepository.findById(1L)).thenReturn(Optional.of(examination));
        when(userRepository.findByUsername(callerUsername)).thenReturn(Optional.of(callerUser));
        when(doctorRepository.findByUserId(2L)).thenReturn(Optional.of(callerDoctor));

        // Act & Assert
        assertThatThrownBy(() -> sickLeaveService.createSickLeave(dto, callerUsername))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not authorized");
    }

    @Test
    void updateSickLeave_whenOwner_returnsUpdatedSickLeaveResponseDTO() {
        // Arrange
        String username = "doctor1";
        User user = buildUser(1L, username);
        Doctor doctor = buildDoctor(1L, username);
        doctor.setUser(user);
        Examination examination = buildExamination(1L, doctor, buildPatient(1L));
        SickLeave sickLeave = buildSickLeave(1L, examination);

        SickLeaveRequestDTO dto = new SickLeaveRequestDTO(1L, LocalDate.now().plusDays(1), 14);

        SickLeave updatedLeave = new SickLeave();
        updatedLeave.setId(1L);
        updatedLeave.setStartDate(dto.getStartDate());
        updatedLeave.setDurationDays(14);
        updatedLeave.setExamination(examination);

        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(doctor));
        when(sickLeaveRepository.save(any(SickLeave.class))).thenReturn(updatedLeave);

        // Act
        SickLeaveResponseDTO result = sickLeaveService.updateSickLeave(1L, dto, username);

        // Assert
        assertThat(result.getDurationDays()).isEqualTo(14);
    }

    @Test
    void updateSickLeave_whenNotOwner_throwsAccessDeniedException() {
        // Arrange
        String callerUsername = "differentDoctor";
        User callerUser = buildUser(2L, callerUsername);
        Doctor callerDoctor = buildDoctor(2L, callerUsername);
        callerDoctor.setUser(callerUser);

        Doctor ownerDoctor = buildDoctor(1L, "ownerDoctor");
        Examination examination = buildExamination(1L, ownerDoctor, buildPatient(1L));
        SickLeave sickLeave = buildSickLeave(1L, examination);

        SickLeaveRequestDTO dto = new SickLeaveRequestDTO(1L, LocalDate.now(), 7);

        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));
        when(userRepository.findByUsername(callerUsername)).thenReturn(Optional.of(callerUser));
        when(doctorRepository.findByUserId(2L)).thenReturn(Optional.of(callerDoctor));

        // Act & Assert
        assertThatThrownBy(() -> sickLeaveService.updateSickLeave(1L, dto, callerUsername))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not authorized");
    }

    @Test
    void deleteSickLeave_whenAdminRole_deletesWithoutOwnershipCheck() {
        // Arrange
        Doctor ownerDoctor = buildDoctor(1L, "anyDoctor");
        Examination examination = buildExamination(1L, ownerDoctor, buildPatient(1L));
        SickLeave sickLeave = buildSickLeave(1L, examination);

        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));

        // Act
        sickLeaveService.deleteSickLeave(1L, "adminUser", true);

        // Assert
        verify(sickLeaveRepository).delete(sickLeave);
    }

    @Test
    void deleteSickLeave_whenDoctorIsOwner_deletesSuccessfully() {
        // Arrange
        String username = "doctor1";
        User user = buildUser(1L, username);
        Doctor doctor = buildDoctor(1L, username);
        doctor.setUser(user);
        Examination examination = buildExamination(1L, doctor, buildPatient(1L));
        SickLeave sickLeave = buildSickLeave(1L, examination);

        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(doctor));

        // Act
        sickLeaveService.deleteSickLeave(1L, username, false);

        // Assert
        verify(sickLeaveRepository).delete(sickLeave);
    }

    @Test
    void deleteSickLeave_whenNotOwner_throwsAccessDeniedException() {
        // Arrange
        String callerUsername = "differentDoctor";
        User callerUser = buildUser(2L, callerUsername);
        Doctor callerDoctor = buildDoctor(2L, callerUsername);
        callerDoctor.setUser(callerUser);

        Doctor ownerDoctor = buildDoctor(1L, "ownerDoctor");
        Examination examination = buildExamination(1L, ownerDoctor, buildPatient(1L));
        SickLeave sickLeave = buildSickLeave(1L, examination);

        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));
        when(userRepository.findByUsername(callerUsername)).thenReturn(Optional.of(callerUser));
        when(doctorRepository.findByUserId(2L)).thenReturn(Optional.of(callerDoctor));

        // Act & Assert
        assertThatThrownBy(() -> sickLeaveService.deleteSickLeave(1L, callerUsername, false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not authorized");
    }

    private SickLeave buildSickLeave(Long id, Examination examination) {
        SickLeave leave = new SickLeave();
        leave.setId(id);
        leave.setStartDate(LocalDate.now());
        leave.setDurationDays(7);
        leave.setExamination(examination);
        return leave;
    }

    private Examination buildExamination(Long id, Doctor doctor, Patient patient) {
        Examination exam = new Examination();
        exam.setId(id);
        exam.setExamDate(LocalDateTime.now());
        exam.setPrice(new BigDecimal("100.00"));
        exam.setPaidByNzok(true);
        exam.setDoctor(doctor);
        exam.setPatient(patient);
        return exam;
    }

    private Doctor buildDoctor(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);

        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setName("Dr. " + username);
        doctor.setUser(user);
        return doctor;
    }

    private Patient buildPatient(Long id) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setName("Patient " + id);
        return patient;
    }

    private User buildUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
}
