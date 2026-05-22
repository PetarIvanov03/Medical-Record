package com.medicalrecord.service;

import com.medicalrecord.dto.binding.PatientBindingModel;
import com.medicalrecord.dto.view.PatientViewModel;
import com.medicalrecord.entity.Patient;

import java.util.List;

public interface PatientService {
    void addPatient(PatientBindingModel bindingModel);
    List<PatientViewModel> getAllPatients();
    PatientViewModel getPatientById(Long id);
    Patient findEntityById(Long id);
    void updatePatient(Long id, PatientBindingModel bindingModel);
    void deletePatient(Long id);
}
