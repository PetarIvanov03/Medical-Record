package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.response.PatientHistoryDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;
import com.ivanovp.medical_record.service.PatientService;
import com.ivanovp.medical_record.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(PatientController.class)
@Import(TestSecurityConfig.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Test
    void getAllPatients_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getAllPatients_withPatientRole_returns403() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPatients_withAdminRole_returns200() throws Exception {
        when(patientService.getAllPatients()).thenReturn(List.of());

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAllPatients_withDoctorRole_returns200() throws Exception {
        when(patientService.getAllPatients()).thenReturn(List.of());

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getPatientById_withDoctorRole_returns200() throws Exception {
        PatientResponseDTO dto = new PatientResponseDTO(1L, "patient1", "Ivan Ivanov", "1234567890", true, null);
        when(patientService.getPatientById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getPatientById_withPatientRole_returns403() throws Exception {
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getPatientHistory_withDoctorRole_returns200() throws Exception {
        PatientHistoryDTO dto = new PatientHistoryDTO(1L, "Ivan", "1234567890", List.of());
        when(patientService.getPatientHistory(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/patients/1/history"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "patient1")
    void getMyProfile_withPatientRole_returns200() throws Exception {
        PatientResponseDTO dto = new PatientResponseDTO(1L, "patient1", "Ivan Ivanov", "1234567890", true, null);
        when(patientService.getMyProfile("patient1")).thenReturn(dto);

        mockMvc.perform(get("/api/patients/my-profile"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getMyProfile_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(get("/api/patients/my-profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "patient1")
    void getMyHistory_withPatientRole_returns200() throws Exception {
        PatientHistoryDTO dto = new PatientHistoryDTO(1L, "Ivan", "1234567890", List.of());
        when(patientService.getMyHistory("patient1")).thenReturn(dto);

        mockMvc.perform(get("/api/patients/my-history"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "patient1")
    void changeGp_withPatientRole_returns200() throws Exception {
        mockMvc.perform(put("/api/patients/my-profile/change-gp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"gpId": 1}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void changeGp_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(put("/api/patients/my-profile/change-gp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"gpId": 1}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPatient_withAdminRole_returns201() throws Exception {
        PatientResponseDTO dto = new PatientResponseDTO(1L, "newpatient", "New Patient", "9876543210", true, null);
        when(patientService.createPatient(any())).thenReturn(dto);

        mockMvc.perform(post("/api/admin/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "newpatient",
                                    "password": "password123",
                                    "name": "New Patient",
                                    "egn": "9876543210",
                                    "isInsured": true
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void createPatient_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(post("/api/admin/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "newpatient",
                                    "password": "password123",
                                    "name": "New Patient",
                                    "egn": "9876543210",
                                    "isInsured": true
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePatient_withAdminRole_returns204() throws Exception {
        mockMvc.perform(delete("/api/admin/patients/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void deletePatient_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(delete("/api/admin/patients/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
