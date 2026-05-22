package com.medicalrecord.service.impl;

import com.medicalrecord.dto.binding.PatientBindingModel;
import com.medicalrecord.dto.view.PatientViewModel;
import com.medicalrecord.entity.Doctor;
import com.medicalrecord.entity.Patient;
import com.medicalrecord.entity.UserEntity;
import com.medicalrecord.repository.PatientRepository;
import com.medicalrecord.repository.UserRepository;
import com.medicalrecord.service.DoctorService;
import com.medicalrecord.service.PatientService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final DoctorService doctorService;
    private final ModelMapper modelMapper;

    public PatientServiceImpl(PatientRepository patientRepository, UserRepository userRepository, DoctorService doctorService, ModelMapper modelMapper) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.doctorService = doctorService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void addPatient(PatientBindingModel bindingModel) {
        UserEntity user = userRepository.findById(bindingModel.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found for Patient"));

        Doctor gp = doctorService.findEntityById(bindingModel.getGpId());
        if (!gp.isGp()) {
            throw new IllegalArgumentException("Selected doctor is not a GP");
        }

        Patient patient = modelMapper.map(bindingModel, Patient.class);
        patient.setUser(user);
        patient.setGp(gp);
        patientRepository.save(patient);
    }

    @Override
    public List<PatientViewModel> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(pat -> modelMapper.map(pat, PatientViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public PatientViewModel getPatientById(Long id) {
        return modelMapper.map(findEntityById(id), PatientViewModel.class);
    }

    @Override
    public Patient findEntityById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    }

    @Override
    public void updatePatient(Long id, PatientBindingModel bindingModel) {
        Patient patient = findEntityById(id);

        Doctor gp = doctorService.findEntityById(bindingModel.getGpId());
        if (!gp.isGp()) {
            throw new IllegalArgumentException("Selected doctor is not a GP");
        }

        patient.setEgn(bindingModel.getEgn());
        patient.setName(bindingModel.getName());
        patient.setHasInsurance(bindingModel.getHasInsurance());
        patient.setGp(gp);

        patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
