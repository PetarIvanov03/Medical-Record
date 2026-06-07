package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.dto.response.MostCommonDiagnosisDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;
import com.ivanovp.medical_record.service.StatisticsService;
import com.ivanovp.medical_record.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
@Import(TestSecurityConfig.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatisticsService statisticsService;

    @Test
    void getPatientsByDiagnosis_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/statistics/patients-by-diagnosis").param("diagnosisId", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getPatientsByDiagnosis_withDoctorRole_returns200() throws Exception {
        when(statisticsService.getPatientsByDiagnosis(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/statistics/patients-by-diagnosis").param("diagnosisId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getPatientsByDiagnosis_withPatientRole_returns403() throws Exception {
        mockMvc.perform(get("/api/statistics/patients-by-diagnosis").param("diagnosisId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMostCommonDiagnosis_withAdminRole_returns200() throws Exception {
        MostCommonDiagnosisDTO dto = new MostCommonDiagnosisDTO("J06.9", "Acute respiratory", 5L);
        when(statisticsService.getMostCommonDiagnosis()).thenReturn(dto);

        mockMvc.perform(get("/api/statistics/most-common-diagnosis"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getMostCommonDiagnosis_withPatientRole_returns403() throws Exception {
        mockMvc.perform(get("/api/statistics/most-common-diagnosis"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPatientsByGp_withAdminRole_returns200() throws Exception {
        when(statisticsService.getPatientsByGp(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/statistics/patients-by-gp").param("gpId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getGpPatientCounts_withAdminRole_returns200() throws Exception {
        when(statisticsService.getGpPatientCounts()).thenReturn(List.of());

        mockMvc.perform(get("/api/statistics/gp-patient-counts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getGpPatientCounts_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(get("/api/statistics/gp-patient-counts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDoctorVisitCounts_withAdminRole_returns200() throws Exception {
        when(statisticsService.getDoctorVisitCounts()).thenReturn(List.of());

        mockMvc.perform(get("/api/statistics/doctor-visit-counts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTotalRevenue_withAdminRole_returns200() throws Exception {
        when(statisticsService.getTotalRevenue()).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/api/statistics/total-revenue"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getTotalRevenue_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(get("/api/statistics/total-revenue"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPeakSickLeaveMonth_withAdminRole_returns200() throws Exception {
        when(statisticsService.getPeakSickLeaveMonth()).thenReturn(3);

        mockMvc.perform(get("/api/statistics/peak-sick-leave-month"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDoctorsWithMostSickLeaves_withAdminRole_returns200() throws Exception {
        when(statisticsService.getDoctorsWithMostSickLeaves()).thenReturn(List.of());

        mockMvc.perform(get("/api/statistics/doctors-with-most-sick-leaves"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getExaminationsByPeriod_withDoctorRole_returns200() throws Exception {
        when(statisticsService.getExaminationsByPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/statistics/examinations-by-period")
                        .param("from", "2024-01-01T00:00:00")
                        .param("to", "2024-12-31T23:59:59"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getExaminationsByPeriod_withPatientRole_returns403() throws Exception {
        mockMvc.perform(get("/api/statistics/examinations-by-period")
                        .param("from", "2024-01-01T00:00:00")
                        .param("to", "2024-12-31T23:59:59"))
                .andExpect(status().isForbidden());
    }
}
