package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.DoctorCreateDTO;
import com.ivanovp.medical_record.dto.request.DoctorUpdateDTO;
import com.ivanovp.medical_record.dto.response.DoctorResponseDTO;

import java.util.List;

public interface DoctorService {

    List<DoctorResponseDTO> getAllDoctors();

    DoctorResponseDTO getDoctorById(Long id);

    DoctorResponseDTO createDoctor(DoctorCreateDTO dto);

    DoctorResponseDTO updateDoctor(Long id, DoctorUpdateDTO dto);

    void deleteDoctor(Long id);
}
