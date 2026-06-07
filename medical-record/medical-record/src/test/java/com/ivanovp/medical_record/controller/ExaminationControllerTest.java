package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.service.ExaminationService;
import com.ivanovp.medical_record.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExaminationController.class)
@Import(TestSecurityConfig.class)
class ExaminationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExaminationService examinationService;

    private ExaminationResponseDTO sampleExamination() {
        return new ExaminationResponseDTO(1L, LocalDateTime.now(), "Dr. Smith", "Patient", null, null, "Treatment", new BigDecimal("100.00"), true);
    }

    @Test
    void getAllExaminations_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/examinations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllExaminations_withAdminRole_returns200() throws Exception {
        when(examinationService.getAllExaminations()).thenReturn(List.of(sampleExamination()));

        mockMvc.perform(get("/api/examinations"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAllExaminations_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(get("/api/examinations"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getAllExaminations_withPatientRole_returns403() throws Exception {
        mockMvc.perform(get("/api/examinations"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "doctor1")
    void getDoctorExaminations_withDoctorRole_returns200() throws Exception {
        when(examinationService.getDoctorExaminations("doctor1")).thenReturn(List.of(sampleExamination()));

        mockMvc.perform(get("/api/examinations/my-history"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDoctorExaminations_withAdminRole_returns403() throws Exception {
        mockMvc.perform(get("/api/examinations/my-history"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getExaminationById_withDoctorRole_returns200() throws Exception {
        when(examinationService.getExaminationById(1L)).thenReturn(sampleExamination());

        mockMvc.perform(get("/api/examinations/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getExaminationById_withPatientRole_returns200() throws Exception {
        when(examinationService.getExaminationById(1L)).thenReturn(sampleExamination());

        mockMvc.perform(get("/api/examinations/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "doctor1")
    void createExamination_withDoctorRole_returns201() throws Exception {
        when(examinationService.createExamination(any(), anyString())).thenReturn(sampleExamination());

        mockMvc.perform(post("/api/examinations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "patientId": 1,
                                    "price": 100.00
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createExamination_withAdminRole_returns403() throws Exception {
        mockMvc.perform(post("/api/examinations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "patientId": 1,
                                    "price": 100.00
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void createExamination_withPatientRole_returns403() throws Exception {
        mockMvc.perform(post("/api/examinations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "patientId": 1,
                                    "price": 100.00
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "doctor1")
    void updateExamination_withDoctorRole_returns200() throws Exception {
        when(examinationService.updateExamination(anyLong(), any(), anyString())).thenReturn(sampleExamination());

        mockMvc.perform(put("/api/examinations/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "patientId": 1,
                                    "price": 150.00
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteExamination_withAdminRole_returns204() throws Exception {
        mockMvc.perform(delete("/api/admin/examinations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void deleteExamination_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(delete("/api/admin/examinations/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
