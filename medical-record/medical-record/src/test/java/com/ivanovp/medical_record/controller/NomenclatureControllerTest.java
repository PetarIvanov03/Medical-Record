package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.response.DiagnosisResponseDTO;
import com.ivanovp.medical_record.dto.response.SpecialtyResponseDTO;
import com.ivanovp.medical_record.service.NomenclatureService;
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

@WebMvcTest(NomenclatureController.class)
@Import(TestSecurityConfig.class)
class NomenclatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NomenclatureService nomenclatureService;

    @Test
    void getAllSpecialties_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAllSpecialties_withDoctorRole_returns200() throws Exception {
        when(nomenclatureService.getAllSpecialties()).thenReturn(List.of(new SpecialtyResponseDTO(1L, "Cardiology")));

        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getAllSpecialties_withPatientRole_returns403() throws Exception {
        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSpecialty_withAdminRole_returns201() throws Exception {
        when(nomenclatureService.createSpecialty(any())).thenReturn(new SpecialtyResponseDTO(1L, "Cardiology"));

        mockMvc.perform(post("/api/admin/specialties")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Cardiology"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void createSpecialty_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(post("/api/admin/specialties")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Cardiology"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSpecialty_withAdminRole_returns200() throws Exception {
        when(nomenclatureService.updateSpecialty(anyLong(), any())).thenReturn(new SpecialtyResponseDTO(1L, "Updated"));

        mockMvc.perform(put("/api/admin/specialties/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Updated"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSpecialty_withAdminRole_returns204() throws Exception {
        mockMvc.perform(delete("/api/admin/specialties/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDiagnoses_withAdminRole_returns200() throws Exception {
        when(nomenclatureService.getAllDiagnoses()).thenReturn(List.of());

        mockMvc.perform(get("/api/diagnoses"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getAllDiagnoses_withPatientRole_returns403() throws Exception {
        mockMvc.perform(get("/api/diagnoses"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "doctor1")
    void getMySpecialtyDiagnoses_withDoctorRole_returns200() throws Exception {
        when(nomenclatureService.getMySpecialtyDiagnoses("doctor1")).thenReturn(List.of());

        mockMvc.perform(get("/api/diagnoses/my-specialty"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getMySpecialtyDiagnoses_withPatientRole_returns403() throws Exception {
        mockMvc.perform(get("/api/diagnoses/my-specialty"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDiagnosis_withAdminRole_returns201() throws Exception {
        DiagnosisResponseDTO dto = new DiagnosisResponseDTO(1L, "J06.9", "Acute respiratory", null);
        when(nomenclatureService.createDiagnosis(any())).thenReturn(dto);

        mockMvc.perform(post("/api/admin/diagnoses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "code": "J06.9",
                                    "name": "Acute respiratory infection"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void createDiagnosis_withDoctorRole_returns403() throws Exception {
        mockMvc.perform(post("/api/admin/diagnoses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "code": "J06.9",
                                    "name": "Acute respiratory infection"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDiagnosis_withAdminRole_returns200() throws Exception {
        DiagnosisResponseDTO dto = new DiagnosisResponseDTO(1L, "J06.0", "Updated", null);
        when(nomenclatureService.updateDiagnosis(anyLong(), any())).thenReturn(dto);

        mockMvc.perform(put("/api/admin/diagnoses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "code": "J06.0",
                                    "name": "Updated"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDiagnosis_withAdminRole_returns204() throws Exception {
        mockMvc.perform(delete("/api/admin/diagnoses/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
