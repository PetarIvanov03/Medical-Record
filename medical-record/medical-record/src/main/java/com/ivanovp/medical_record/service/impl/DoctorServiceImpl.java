package com.ivanovp.medical_record.service.impl;

import com.ivanovp.medical_record.dto.request.DoctorCreateDTO;
import com.ivanovp.medical_record.dto.request.DoctorUpdateDTO;
import com.ivanovp.medical_record.dto.response.DoctorResponseDTO;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Specialty;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.SpecialtyRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.DoctorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             SpecialtyRepository specialtyRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToDoctorResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return mapToDoctorResponseDTO(doctor);
    }

    @Override
    public DoctorResponseDTO createDoctor(DoctorCreateDTO dto) {
        // Check if username is already taken
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken: " + dto.getUsername());
        }

        // Find specialty
        Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + dto.getSpecialtyId()));

        // Create User
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.DOCTOR);
        user = userRepository.save(user);

        // Create Doctor
        Doctor doctor = new Doctor();
        doctor.setUin(dto.getUin());
        doctor.setName(dto.getName());
        doctor.setSpecialty(specialty);
        doctor.setGp(dto.isGp());
        doctor.setUser(user);
        doctor = doctorRepository.save(doctor);

        return mapToDoctorResponseDTO(doctor);
    }

    @Override
    public DoctorResponseDTO updateDoctor(Long id, DoctorUpdateDTO dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        // Update name
        doctor.setName(dto.getName());

        // Update specialty
        Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + dto.getSpecialtyId()));
        doctor.setSpecialty(specialty);

        // Update isGp
        doctor.setGp(dto.isGp());

        doctor = doctorRepository.save(doctor);

        return mapToDoctorResponseDTO(doctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        // Delete linked user if exists
        if (doctor.getUser() != null) {
            userRepository.delete(doctor.getUser());
        }

        doctorRepository.delete(doctor);
    }

    private DoctorResponseDTO mapToDoctorResponseDTO(Doctor doctor) {
        String specialtyName = null;
        if (doctor.getSpecialty() != null) {
            specialtyName = doctor.getSpecialty().getName();
        }
        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getUin(),
                doctor.getName(),
                specialtyName,
                doctor.isGp()
        );
    }
}

