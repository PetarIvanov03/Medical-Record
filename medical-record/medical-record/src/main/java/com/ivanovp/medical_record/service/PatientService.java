package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.ChangeGpDTO;
import com.ivanovp.medical_record.dto.request.PatientCreateDTO;
import com.ivanovp.medical_record.dto.response.PatientHistoryDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;

import java.util.List;

public interface PatientService {

    List<PatientResponseDTO> getAllPatients();

    PatientResponseDTO getPatientById(Long id);

    PatientResponseDTO getMyProfile(String username);

    void changeGp(String username, ChangeGpDTO dto);

    PatientHistoryDTO getPatientHistory(Long id);

    PatientHistoryDTO getMyHistory(String username);

    PatientResponseDTO createPatient(PatientCreateDTO dto);

    void deletePatient(Long id);
}
