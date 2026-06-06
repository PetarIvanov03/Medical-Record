package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.ExaminationRequestDTO;
import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;

import java.util.List;

public interface ExaminationService {

    List<ExaminationResponseDTO> getAllExaminations();

    List<ExaminationResponseDTO> getDoctorExaminations(String username);

    ExaminationResponseDTO getExaminationById(Long id);

    ExaminationResponseDTO createExamination(ExaminationRequestDTO dto, String username);

    ExaminationResponseDTO updateExamination(Long id, ExaminationRequestDTO dto, String username);

    void deleteExamination(Long id);
}
