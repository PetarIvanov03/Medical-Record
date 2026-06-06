package com.ivanovp.medical_record.service.impl;

import com.ivanovp.medical_record.dto.request.ChangeGpDTO;
import com.ivanovp.medical_record.dto.request.PatientCreateDTO;
import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.dto.response.PatientHistoryDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.entity.UserRole;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.PatientRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.PatientService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientServiceImpl(PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToPatientResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return mapToPatientResponseDTO(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user: " + username));
        return mapToPatientResponseDTO(patient);
    }

    @Override
    public void changeGp(String username, ChangeGpDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user: " + username));

        Doctor gp = doctorRepository.findById(dto.getGpId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getGpId()));
        if (!gp.isGp()) {
            throw new IllegalArgumentException("Doctor with id " + dto.getGpId() + " is not a GP");
        }

        patient.setGp(gp);
        patientRepository.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientHistoryDTO getPatientHistory(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        return new PatientHistoryDTO(
                patient.getId(),
                patient.getName(),
                patient.getEgn(),
                Collections.emptyList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PatientHistoryDTO getMyHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user: " + username));

        return new PatientHistoryDTO(
                patient.getId(),
                patient.getName(),
                patient.getEgn(),
                Collections.emptyList()
        );
    }

    @Override
    public PatientResponseDTO createPatient(PatientCreateDTO dto) {
        // Check if username is already taken
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken: " + dto.getUsername());
        }

        // Create User
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.PATIENT);
        user = userRepository.save(user);

        // Create Patient
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEgn(dto.getEgn());
        patient.setInsured(dto.isInsured());
        patient.setUser(user);

        if (dto.getGpId() != null) {
            Doctor gp = doctorRepository.findById(dto.getGpId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getGpId()));
            if (!gp.isGp()) {
                throw new IllegalArgumentException("Doctor with id " + dto.getGpId() + " is not a GP");
            }
            patient.setGp(gp);
        }

        patient = patientRepository.save(patient);
        return mapToPatientResponseDTO(patient);
    }

    @Override
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // Delete linked user if exists
        if (patient.getUser() != null) {
            userRepository.delete(patient.getUser());
        }

        patientRepository.delete(patient);
    }

    private PatientResponseDTO mapToPatientResponseDTO(Patient patient) {
        String gpName = null;
        if (patient.getGp() != null) {
            gpName = patient.getGp().getName();
        }
        return new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getEgn(),
                patient.isInsured(),
                gpName
        );
    }
}
