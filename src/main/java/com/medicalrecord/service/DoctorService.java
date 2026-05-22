package com.medicalrecord.service;

import com.medicalrecord.dto.binding.DoctorBindingModel;
import com.medicalrecord.dto.view.DoctorViewModel;
import com.medicalrecord.entity.Doctor;

import java.util.List;

public interface DoctorService {
    void addDoctor(DoctorBindingModel bindingModel);
    List<DoctorViewModel> getAllDoctors();
    DoctorViewModel getDoctorById(Long id);
    Doctor findEntityById(Long id);
    void updateDoctor(Long id, DoctorBindingModel bindingModel);
    void deleteDoctor(Long id);
}
