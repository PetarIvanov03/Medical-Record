package com.medicalrecord.service.impl;

import com.medicalrecord.dto.binding.DoctorBindingModel;
import com.medicalrecord.dto.view.DoctorViewModel;
import com.medicalrecord.entity.Doctor;
import com.medicalrecord.entity.UserEntity;
import com.medicalrecord.repository.DoctorRepository;
import com.medicalrecord.repository.UserRepository;
import com.medicalrecord.service.DoctorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void addDoctor(DoctorBindingModel bindingModel) {
        UserEntity user = userRepository.findById(bindingModel.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found for Doctor"));

        Doctor doctor = modelMapper.map(bindingModel, Doctor.class);
        doctor.setUser(user);
        doctorRepository.save(doctor);
    }

    @Override
    public List<DoctorViewModel> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doc -> modelMapper.map(doc, DoctorViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public DoctorViewModel getDoctorById(Long id) {
        return modelMapper.map(findEntityById(id), DoctorViewModel.class);
    }

    @Override
    public Doctor findEntityById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
    }

    @Override
    public void updateDoctor(Long id, DoctorBindingModel bindingModel) {
        Doctor doctor = findEntityById(id);
        doctor.setUin(bindingModel.getUin());
        doctor.setName(bindingModel.getName());
        doctor.setSpecialty(bindingModel.getSpecialty());
        doctor.setGp(bindingModel.getGp());
        doctorRepository.save(doctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }
}
