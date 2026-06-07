package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.DiagnosisRequestDTO;
import com.ivanovp.medical_record.dto.request.SpecialtyRequestDTO;
import com.ivanovp.medical_record.dto.response.DiagnosisResponseDTO;
import com.ivanovp.medical_record.dto.response.SpecialtyResponseDTO;
import com.ivanovp.medical_record.entity.Diagnosis;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Specialty;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.DiagnosisRepository;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.SpecialtyRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.impl.NomenclatureServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NomenclatureServiceTest {

    @Mock private SpecialtyRepository specialtyRepository;
    @Mock private DiagnosisRepository diagnosisRepository;
    @Mock private UserRepository userRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private ExaminationRepository examinationRepository;
    @InjectMocks private NomenclatureServiceImpl nomenclatureService;

    @Test
    void getAllSpecialties_returnsListOfSpecialtyResponseDTOs() {
        // Arrange
        Specialty specialty = buildSpecialty(1L, "Cardiology");
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));

        // Act
        List<SpecialtyResponseDTO> result = nomenclatureService.getAllSpecialties();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cardiology");
    }

    @Test
    void getAllDiagnoses_returnsListOfDiagnosisResponseDTOs() {
        // Arrange
        Diagnosis diagnosis = buildDiagnosis(1L, "J06.9", "Acute upper respiratory infection");
        when(diagnosisRepository.findAll()).thenReturn(List.of(diagnosis));

        // Act
        List<DiagnosisResponseDTO> result = nomenclatureService.getAllDiagnoses();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("J06.9");
    }

    @Test
    void getMySpecialtyDiagnoses_whenDoctorHasSpecialtyWithDiagnoses_returnsDiagnosisList() {
        // Arrange
        String username = "doctor1";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Diagnosis diagnosis = buildDiagnosis(1L, "J06.9", "Acute upper respiratory infection");
        Specialty specialty = buildSpecialty(1L, "General Medicine");
        specialty.getDiagnoses().add(diagnosis);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setSpecialty(specialty);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(doctor));

        // Act
        List<DiagnosisResponseDTO> result = nomenclatureService.getMySpecialtyDiagnoses(username);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("J06.9");
    }

    @Test
    void getMySpecialtyDiagnoses_whenUserNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> nomenclatureService.getMySpecialtyDiagnoses("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void createSpecialty_savesAndReturnsSpecialtyResponseDTO() {
        // Arrange
        SpecialtyRequestDTO dto = new SpecialtyRequestDTO("Cardiology");
        Specialty saved = buildSpecialty(1L, "Cardiology");
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(saved);

        // Act
        SpecialtyResponseDTO result = nomenclatureService.createSpecialty(dto);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Cardiology");
    }

    @Test
    void updateSpecialty_whenExists_returnsUpdatedSpecialtyResponseDTO() {
        // Arrange
        SpecialtyRequestDTO dto = new SpecialtyRequestDTO("Updated Name");
        Specialty existing = buildSpecialty(1L, "Old Name");
        Specialty updated = buildSpecialty(1L, "Updated Name");

        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(updated);

        // Act
        SpecialtyResponseDTO result = nomenclatureService.updateSpecialty(1L, dto);

        // Assert
        assertThat(result.getName()).isEqualTo("Updated Name");
    }

    @Test
    void updateSpecialty_whenNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(specialtyRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> nomenclatureService.updateSpecialty(99L, new SpecialtyRequestDTO("Name")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteSpecialty_whenAssignedToDoctors_throwsIllegalArgumentException() {
        // Arrange
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(buildSpecialty(1L, "Cardiology")));
        when(doctorRepository.existsBySpecialtyId(1L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> nomenclatureService.deleteSpecialty(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot delete");
    }

    @Test
    void deleteSpecialty_whenExistsAndNotAssigned_deletesSuccessfully() {
        // Arrange
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(buildSpecialty(1L, "Cardiology")));
        when(doctorRepository.existsBySpecialtyId(1L)).thenReturn(false);

        // Act
        nomenclatureService.deleteSpecialty(1L);

        // Assert
        verify(specialtyRepository).deleteById(1L);
    }

    @Test
    void createDiagnosis_withoutSpecialty_returnsDiagnosisResponseDTO() {
        // Arrange
        DiagnosisRequestDTO dto = new DiagnosisRequestDTO("J06.9", "Acute respiratory", null);
        Diagnosis saved = buildDiagnosis(1L, "J06.9", "Acute respiratory");
        when(diagnosisRepository.save(any(Diagnosis.class))).thenReturn(saved);

        // Act
        DiagnosisResponseDTO result = nomenclatureService.createDiagnosis(dto);

        // Assert
        assertThat(result.getCode()).isEqualTo("J06.9");
        assertThat(result.getName()).isEqualTo("Acute respiratory");
    }

    @Test
    void updateDiagnosis_whenExists_returnsUpdatedDiagnosisResponseDTO() {
        // Arrange
        DiagnosisRequestDTO dto = new DiagnosisRequestDTO("J06.0", "Updated Name", null);
        Diagnosis existing = buildDiagnosis(1L, "J06.9", "Old Name");
        Diagnosis updated = buildDiagnosis(1L, "J06.0", "Updated Name");

        when(diagnosisRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(diagnosisRepository.save(any(Diagnosis.class))).thenReturn(updated);

        // Act
        DiagnosisResponseDTO result = nomenclatureService.updateDiagnosis(1L, dto);

        // Assert
        assertThat(result.getCode()).isEqualTo("J06.0");
        assertThat(result.getName()).isEqualTo("Updated Name");
    }

    @Test
    void updateDiagnosis_whenNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(diagnosisRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> nomenclatureService.updateDiagnosis(99L, new DiagnosisRequestDTO("X", "Y", null)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteDiagnosis_whenExists_deletesAndCleansRelations() {
        // Arrange
        Diagnosis diagnosis = buildDiagnosis(1L, "J06.9", "Acute respiratory");
        when(diagnosisRepository.findById(1L)).thenReturn(Optional.of(diagnosis));
        when(examinationRepository.findByDiagnosisId(1L)).thenReturn(List.of());

        // Act
        nomenclatureService.deleteDiagnosis(1L);

        // Assert
        verify(diagnosisRepository).delete(diagnosis);
    }

    private Specialty buildSpecialty(Long id, String name) {
        Specialty specialty = new Specialty();
        specialty.setId(id);
        specialty.setName(name);
        specialty.setDiagnoses(new ArrayList<>());
        return specialty;
    }

    private Diagnosis buildDiagnosis(Long id, String code, String name) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(id);
        diagnosis.setCode(code);
        diagnosis.setName(name);
        diagnosis.setSpecialties(new ArrayList<>());
        return diagnosis;
    }
}
