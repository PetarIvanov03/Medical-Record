package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.request.SickLeaveRequestDTO;
import com.ivanovp.medical_record.dto.response.SickLeaveResponseDTO;

public interface SickLeaveService {

    SickLeaveResponseDTO getSickLeaveById(Long id);

    SickLeaveResponseDTO createSickLeave(SickLeaveRequestDTO dto, String username);

    SickLeaveResponseDTO updateSickLeave(Long id, SickLeaveRequestDTO dto, String username);
    
    void deleteSickLeave(Long id, String username, boolean isAdmin);
}
