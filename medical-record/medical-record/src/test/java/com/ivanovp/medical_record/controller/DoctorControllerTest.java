package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.request.DoctorCreateDTO;
import com.ivanovp.medical_record.dto.response.DoctorResponseDTO;
import com.ivanovp.medical_record.service.DoctorService;
import com.ivanovp.medical_record.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
@Import(TestSecurityConfig.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @Test
    void getAllDoctors_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void createDoctor_withDoctorRole_returns403() throws Exception {
        DoctorCreateDTO dto = new DoctorCreateDTO();
        dto.setUin("1234567890");
        dto.setName("Test Doctor");
        dto.setSpecialtyId(1L);
        dto.setGp(false);
        dto.setUsername("testdoctor");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/admin/doctors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "uin": "1234567890",
                                    "name": "Test Doctor",
                                    "specialtyId": 1,
                                    "isGp": false,
                                    "username": "testdoctor",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDoctor_withAdminRole_returns201() throws Exception {
        DoctorCreateDTO dto = new DoctorCreateDTO();
        dto.setUin("1234567890");
        dto.setName("Test Doctor");
        dto.setSpecialtyId(1L);
        dto.setGp(false);
        dto.setUsername("testdoctor");
        dto.setPassword("password123");

        DoctorResponseDTO response = new DoctorResponseDTO(1L, "1234567890", "Test Doctor", "Cardiology", false);
        when(doctorService.createDoctor(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/doctors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "uin": "1234567890",
                                    "name": "Test Doctor",
                                    "specialtyId": 1,
                                    "isGp": false,
                                    "username": "testdoctor",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isCreated());
    }
}
