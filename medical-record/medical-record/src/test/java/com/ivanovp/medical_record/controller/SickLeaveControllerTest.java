package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.response.SickLeaveResponseDTO;
import com.ivanovp.medical_record.service.SickLeaveService;
import com.ivanovp.medical_record.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SickLeaveController.class)
@Import(TestSecurityConfig.class)
class SickLeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SickLeaveService sickLeaveService;

    @Test
    void getSickLeaveById_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/sick-leaves/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getSickLeaveById_withDoctorRole_returns200() throws Exception {
        SickLeaveResponseDTO dto = new SickLeaveResponseDTO(1L, LocalDate.now(), 7, 1L, "Patient", "Doctor");
        when(sickLeaveService.getSickLeaveById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/sick-leaves/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getSickLeaveById_withPatientRole_returns200() throws Exception {
        SickLeaveResponseDTO dto = new SickLeaveResponseDTO(1L, LocalDate.now(), 7, 1L, "Patient", "Doctor");
        when(sickLeaveService.getSickLeaveById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/sick-leaves/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "doctor1")
    void createSickLeave_withDoctorRole_returns201() throws Exception {
        SickLeaveResponseDTO dto = new SickLeaveResponseDTO(1L, LocalDate.now(), 7, 1L, "Patient", "Doctor");
        when(sickLeaveService.createSickLeave(any(), anyString())).thenReturn(dto);

        mockMvc.perform(post("/api/sick-leaves")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "examinationId": 1,
                                    "startDate": "2024-03-15",
                                    "durationDays": 7
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void createSickLeave_withPatientRole_returns403() throws Exception {
        mockMvc.perform(post("/api/sick-leaves")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "examinationId": 1,
                                    "startDate": "2024-03-15",
                                    "durationDays": 7
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "doctor1")
    void updateSickLeave_withDoctorRole_returns200() throws Exception {
        SickLeaveResponseDTO dto = new SickLeaveResponseDTO(1L, LocalDate.now().plusDays(1), 14, 1L, "Patient", "Doctor");
        when(sickLeaveService.updateSickLeave(anyLong(), any(), anyString())).thenReturn(dto);

        mockMvc.perform(put("/api/sick-leaves/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "examinationId": 1,
                                    "startDate": "2024-03-20",
                                    "durationDays": 14
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    void deleteSickLeave_withAdminRole_returns204() throws Exception {
        mockMvc.perform(delete("/api/sick-leaves/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void deleteSickLeave_withPatientRole_returns403() throws Exception {
        mockMvc.perform(delete("/api/sick-leaves/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void createSickLeave_withInvalidDurationDays_returns400() throws Exception {
        mockMvc.perform(post("/api/sick-leaves")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "examinationId": 1,
                                    "startDate": "2024-03-15",
                                    "durationDays": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
