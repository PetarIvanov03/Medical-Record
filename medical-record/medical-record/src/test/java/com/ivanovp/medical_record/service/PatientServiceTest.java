package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.ChangeGpDTO;
import com.ivanovp.medical_record.dto.request.PatientCreateDTO;
import com.ivanovp.medical_record.dto.response.PatientHistoryDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.PatientRepository;
import com.ivanovp.medical_record.repository.SickLeaveRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.impl.PatientServiceImpl;
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
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ExaminationRepository examinationRepository;
    @Mock private SickLeaveRepository sickLeaveRepository;
    @InjectMocks private PatientServiceImpl patientService;

    @Test
    void getAllPatients_returnsListOfPatientResponseDTOs() {
        // Arrange
        Patient patient = buildPatient(1L, "1234567890", "Ivan Ivanov", true);
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        // Act
        List<PatientResponseDTO> result = patientService.getAllPatients();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ivan Ivanov");
    }

    @Test
    void getPatientById_whenPatientExists_returnsPatientResponseDTO() {
        // Arrange
        Patient patient = buildPatient(1L, "1234567890", "Ivan Ivanov", true);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        PatientResponseDTO result = patientService.getPatientById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Ivan Ivanov");
    }

    @Test
    void getPatientById_whenPatientNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getMyProfile_whenUserAndPatientExist_returnsPatientResponseDTO() {
        // Arrange
        User user = buildUser(1L, "patient1");
        Patient patient = buildPatient(1L, "1234567890", "Ivan Ivanov", true);
        patient.setUser(user);

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));

        // Act
        PatientResponseDTO result = patientService.getMyProfile("patient1");

        // Assert
        assertThat(result.getName()).isEqualTo("Ivan Ivanov");
        assertThat(result.getUsername()).isEqualTo("patient1");
    }

    @Test
    void getMyProfile_whenUserNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.getMyProfile("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void getPatientHistory_whenPatientExists_returnsHistoryWithEmptyExaminations() {
        // Arrange
        Patient patient = buildPatient(1L, "1234567890", "Ivan Ivanov", true);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(examinationRepository.findByPatientId(1L)).thenReturn(List.of());

        // Act
        PatientHistoryDTO result = patientService.getPatientHistory(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getExaminations()).isEmpty();
    }

    @Test
    void changeGp_whenDoctorIsGp_updatesPatientGp() {
        // Arrange
        User user = buildUser(1L, "patient1");
        Patient patient = buildPatient(1L, "1234567890", "Ivan Ivanov", true);
        patient.setUser(user);

        Doctor gpDoctor = new Doctor();
        gpDoctor.setId(5L);
        gpDoctor.setGp(true);
        gpDoctor.setName("Dr. GP");

        ChangeGpDTO dto = new ChangeGpDTO(5L);

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(5L)).thenReturn(Optional.of(gpDoctor));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        patientService.changeGp("patient1", dto);

        // Assert
        verify(patientRepository).save(patient);
        assertThat(patient.getGp()).isEqualTo(gpDoctor);
    }

    @Test
    void changeGp_whenDoctorIsNotGp_throwsIllegalArgumentException() {
        // Arrange
        User user = buildUser(1L, "patient1");
        Patient patient = buildPatient(1L, "1234567890", "Ivan Ivanov", true);
        patient.setUser(user);

        Doctor nonGpDoctor = new Doctor();
        nonGpDoctor.setId(5L);
        nonGpDoctor.setGp(false);

        ChangeGpDTO dto = new ChangeGpDTO(5L);

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(5L)).thenReturn(Optional.of(nonGpDoctor));

        // Act & Assert
        assertThatThrownBy(() -> patientService.changeGp("patient1", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a GP");
    }

    @Test
    void changeGp_whenGpNotFound_throwsResourceNotFoundException() {
        // Arrange
        User user = buildUser(1L, "patient1");
        Patient patient = buildPatient(1L, "1234567890", "Ivan Ivanov", true);
        patient.setUser(user);

        ChangeGpDTO dto = new ChangeGpDTO(99L);

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.changeGp("patient1", dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createPatient_whenUsernameAvailable_returnsPatientResponseDTO() {
        // Arrange
        PatientCreateDTO dto = new PatientCreateDTO("newpatient", "password123", "New Patient", "9876543210", true, null);

        User savedUser = buildUser(1L, "newpatient");
        Patient savedPatient = buildPatient(1L, "9876543210", "New Patient", true);
        savedPatient.setUser(savedUser);

        when(userRepository.findByUsername("newpatient")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        // Act
        PatientResponseDTO result = patientService.createPatient(dto);

        // Assert
        assertThat(result.getName()).isEqualTo("New Patient");
        assertThat(result.getEgn()).isEqualTo("9876543210");
    }

    @Test
    void createPatient_whenUsernameAlreadyTaken_throwsIllegalArgumentException() {
        // Arrange
        PatientCreateDTO dto = new PatientCreateDTO("existing", "password123", "New Patient", "9876543210", true, null);
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThatThrownBy(() -> patientService.createPatient(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existing");
    }

    @Test
    void createPatient_whenGpIdProvidedButNotGpDoctor_throwsIllegalArgumentException() {
        // Arrange
        PatientCreateDTO dto = new PatientCreateDTO("newpatient", "password123", "New Patient", "9876543210", true, 5L);

        User savedUser = buildUser(1L, "newpatient");
        Doctor nonGpDoctor = new Doctor();
        nonGpDoctor.setId(5L);
        nonGpDoctor.setGp(false);

        when(userRepository.findByUsername("newpatient")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(doctorRepository.findById(5L)).thenReturn(Optional.of(nonGpDoctor));

        // Act & Assert
        assertThatThrownBy(() -> patientService.createPatient(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a GP");
    }

    @Test
    void updateInsuranceStatus_whenPatientExists_updatesInsuranceFlag() {
        // Arrange
        Patient patient = buildPatient(1L, "1234567890", "Ivan Ivanov", false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        PatientResponseDTO result = patientService.updateInsuranceStatus(1L, true);

        // Assert
        assertThat(result.isInsured()).isTrue();
    }

    @Test
    void deletePatient_whenPatientExists_deletesPatientAndUser() {
        // Arrange
        User user = buildUser(1L, "patient1");
        Patient patient = buildPatient(1L, "1234567890", "Ivan Ivanov", true);
        patient.setUser(user);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(examinationRepository.findByPatientId(1L)).thenReturn(List.of());

        // Act
        patientService.deletePatient(1L);

        // Assert
        verify(patientRepository).delete(patient);
        verify(userRepository).delete(user);
    }

    @Test
    void deletePatient_whenPatientNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.deletePatient(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    private Patient buildPatient(Long id, String egn, String name, boolean insured) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setEgn(egn);
        patient.setName(name);
        patient.setInsured(insured);
        return patient;
    }

    private User buildUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(UserRole.PATIENT);
        return user;
    }
}
