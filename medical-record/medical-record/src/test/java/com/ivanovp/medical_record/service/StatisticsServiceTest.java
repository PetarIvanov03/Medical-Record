package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.dto.response.MostCommonDiagnosisDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;
import com.ivanovp.medical_record.dto.response.RevenueDTO;
import com.ivanovp.medical_record.dto.response.StatCountDTO;
import com.ivanovp.medical_record.entity.Diagnosis;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Examination;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.PatientRepository;
import com.ivanovp.medical_record.repository.SickLeaveRepository;
import com.ivanovp.medical_record.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock private ExaminationRepository examinationRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private SickLeaveRepository sickLeaveRepository;
    @Mock private DoctorRepository doctorRepository;
    @InjectMocks private StatisticsServiceImpl statisticsService;

    @Test
    void getPatientsByDiagnosis_returnsMappedPatientResponseDTOs() {
        // Arrange
        Patient patient = buildPatient(1L, "Ivan Ivanov", "1234567890");
        when(examinationRepository.findPatientsByDiagnosisId(1L)).thenReturn(List.of(patient));

        // Act
        List<PatientResponseDTO> result = statisticsService.getPatientsByDiagnosis(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ivan Ivanov");
    }

    @Test
    void getMostCommonDiagnosis_whenDataExists_returnsMostCommonDiagnosisDTO() {
        // Arrange
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(1L);
        diagnosis.setCode("J06.9");
        diagnosis.setName("Acute upper respiratory infection");

        Object[] row = {diagnosis, 5L};
        List<Object[]> rows = Collections.singletonList(row);
        when(examinationRepository.findMostCommonDiagnosisWithCount()).thenReturn(rows);

        // Act
        MostCommonDiagnosisDTO result = statisticsService.getMostCommonDiagnosis();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("J06.9");
        assertThat(result.getName()).isEqualTo("Acute upper respiratory infection");
        assertThat(result.getCount()).isEqualTo(5L);
    }

    @Test
    void getMostCommonDiagnosis_whenNoData_returnsNull() {
        // Arrange
        when(examinationRepository.findMostCommonDiagnosisWithCount()).thenReturn(List.of());

        // Act
        MostCommonDiagnosisDTO result = statisticsService.getMostCommonDiagnosis();

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void getPatientsByGp_returnsMappedPatientResponseDTOs() {
        // Arrange
        Doctor gp = new Doctor();
        gp.setId(1L);
        gp.setName("Dr. GP");

        Patient patient = buildPatient(1L, "Ivan Ivanov", "1234567890");
        patient.setGp(gp);

        when(patientRepository.findByGpId(1L)).thenReturn(List.of(patient));

        // Act
        List<PatientResponseDTO> result = statisticsService.getPatientsByGp(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGpName()).isEqualTo("Dr. GP");
    }

    @Test
    void getGpPatientCounts_returnsMappedStatCountDTOs() {
        // Arrange
        Object[] row = {"Dr. GP", 5L};
        List<Object[]> rows = Collections.singletonList(row);
        when(patientRepository.countPatientsByGp()).thenReturn(rows);

        // Act
        List<StatCountDTO> result = statisticsService.getGpPatientCounts();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Dr. GP");
        assertThat(result.get(0).getCount()).isEqualTo(5L);
    }

    @Test
    void getDoctorVisitCounts_returnsMappedStatCountDTOs() {
        // Arrange
        Object[] row = {"Dr. Smith", 10L};
        List<Object[]> rows = Collections.singletonList(row);
        when(examinationRepository.countVisitsPerDoctor()).thenReturn(rows);

        // Act
        List<StatCountDTO> result = statisticsService.getDoctorVisitCounts();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Dr. Smith");
        assertThat(result.get(0).getCount()).isEqualTo(10L);
    }

    @Test
    void getTotalRevenue_whenRevenueExists_returnsTotalRevenue() {
        // Arrange
        when(examinationRepository.findTotalPatientRevenue()).thenReturn(new BigDecimal("1500.00"));

        // Act
        BigDecimal result = statisticsService.getTotalRevenue();

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    void getTotalRevenue_whenNoExaminations_returnsZero() {
        // Arrange
        when(examinationRepository.findTotalPatientRevenue()).thenReturn(null);

        // Act
        BigDecimal result = statisticsService.getTotalRevenue();

        // Assert
        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getRevenueByDoctor_returnsMappedRevenueDTOs() {
        // Arrange
        Object[] row = {"Dr. Smith", new BigDecimal("500.00")};
        List<Object[]> rows = Collections.singletonList(row);
        when(examinationRepository.findRevenueByDoctor()).thenReturn(rows);

        // Act
        List<RevenueDTO> result = statisticsService.getRevenueByDoctor();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Dr. Smith");
        assertThat(result.get(0).getRevenue()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    void getPeakSickLeaveMonth_returnsPeakMonth() {
        // Arrange
        when(sickLeaveRepository.findPeakSickLeaveMonth()).thenReturn(3);

        // Act
        Integer result = statisticsService.getPeakSickLeaveMonth();

        // Assert
        assertThat(result).isEqualTo(3);
    }

    @Test
    void getDoctorsWithMostSickLeaves_returnsMappedStatCountDTOs() {
        // Arrange
        Object[] row = {"Dr. Smith", 8L};
        List<Object[]> rows = Collections.singletonList(row);
        when(sickLeaveRepository.findDoctorsWithMostSickLeaves()).thenReturn(rows);

        // Act
        List<StatCountDTO> result = statisticsService.getDoctorsWithMostSickLeaves();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabel()).isEqualTo("Dr. Smith");
        assertThat(result.get(0).getCount()).isEqualTo(8L);
    }

    @Test
    void getExaminationsByPeriod_returnsMappedExaminationResponseDTOs() {
        // Arrange
        Examination exam = buildExamination(1L);
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(examinationRepository.findByExamDateBetween(from, to)).thenReturn(List.of(exam));

        // Act
        List<ExaminationResponseDTO> result = statisticsService.getExaminationsByPeriod(from, to);

        // Assert
        assertThat(result).hasSize(1);
    }

    private Patient buildPatient(Long id, String name, String egn) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setName(name);
        patient.setEgn(egn);
        patient.setInsured(true);
        return patient;
    }

    private Examination buildExamination(Long id) {
        Examination exam = new Examination();
        exam.setId(id);
        exam.setExamDate(LocalDateTime.now());
        exam.setPrice(new BigDecimal("100.00"));
        exam.setPaidByNzok(false);
        return exam;
    }
}
