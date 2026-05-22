package com.medicalrecord.service;

import com.medicalrecord.dto.binding.SickLeaveBindingModel;
import com.medicalrecord.dto.view.SickLeaveViewModel;

import java.util.List;

public interface SickLeaveService {
    void issueSickLeave(SickLeaveBindingModel bindingModel);
    List<SickLeaveViewModel> getAllSickLeaves();
    void deleteSickLeave(Long id);
}
