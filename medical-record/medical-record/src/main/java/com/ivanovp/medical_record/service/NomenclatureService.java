package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.DiagnosisRequestDTO;
import com.ivanovp.medical_record.dto.request.SpecialtyRequestDTO;
import com.ivanovp.medical_record.dto.response.DiagnosisResponseDTO;
import com.ivanovp.medical_record.dto.response.SpecialtyResponseDTO;

import java.util.List;

public interface NomenclatureService {

    List<SpecialtyResponseDTO> getAllSpecialties();

    List<DiagnosisResponseDTO> getAllDiagnoses();

    SpecialtyResponseDTO createSpecialty(SpecialtyRequestDTO dto);

    SpecialtyResponseDTO updateSpecialty(Long id, SpecialtyRequestDTO dto);

    void deleteSpecialty(Long id);

    DiagnosisResponseDTO createDiagnosis(DiagnosisRequestDTO dto);

    DiagnosisResponseDTO updateDiagnosis(Long id, DiagnosisRequestDTO dto);

    void deleteDiagnosis(Long id);
}
