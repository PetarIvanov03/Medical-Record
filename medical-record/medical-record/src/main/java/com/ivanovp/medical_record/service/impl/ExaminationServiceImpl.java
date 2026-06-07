package com.ivanovp.medical_record.service.impl;

import com.ivanovp.medical_record.dto.request.ExaminationRequestDTO;
import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.entity.Diagnosis;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Examination;
import com.ivanovp.medical_record.entity.Patient;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.DiagnosisRepository;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.PatientRepository;
import com.ivanovp.medical_record.repository.SickLeaveRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.ExaminationService;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExaminationServiceImpl implements ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final SickLeaveRepository sickLeaveRepository;

    public ExaminationServiceImpl(ExaminationRepository examinationRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  UserRepository userRepository,
                                  DiagnosisRepository diagnosisRepository,
                                  SickLeaveRepository sickLeaveRepository) {
        this.examinationRepository = examinationRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.sickLeaveRepository = sickLeaveRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExaminationResponseDTO> getAllExaminations() {
        return examinationRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExaminationResponseDTO> getDoctorExaminations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user: " + username));
        return examinationRepository.findByDoctorId(doctor.getId()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExaminationResponseDTO getExaminationById(Long id) {
        Examination examination = examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with id: " + id));
        return mapToResponseDTO(examination);
    }

    @Override
    public ExaminationResponseDTO createExamination(ExaminationRequestDTO dto, String username) {
        // Find the doctor by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user: " + username));

        // Find the patient
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));

        // Validate diagnosis if provided
        Diagnosis diagnosis = null;
        if (dto.getDiagnosisId() != null) {
            diagnosis = diagnosisRepository.findById(dto.getDiagnosisId())
                    .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with id: " + dto.getDiagnosisId()));
            // Validate that the diagnosis belongs to the doctor's specialty
            if (doctor.getSpecialty() != null && doctor.getSpecialty().getDiagnoses() != null) {
                boolean valid = doctor.getSpecialty().getDiagnoses().stream()
                        .anyMatch(d -> d.getId().equals(dto.getDiagnosisId()));
                if (!valid) {
                    throw new IllegalArgumentException("Diagnosis with id " + dto.getDiagnosisId()
                            + " is not in the doctor's specialty");
                }
            }
        }

        // Create the examination
        Examination examination = new Examination();
        examination.setExamDate(LocalDateTime.now());
        examination.setTreatment(dto.getTreatment());
        examination.setPrice(dto.getPrice());
        examination.setPaidByNzok(patient.isInsured());
        examination.setDoctor(doctor);
        examination.setPatient(patient);
        examination.setDiagnosis(diagnosis);

        examination = examinationRepository.save(examination);

        return mapToResponseDTO(examination);
    }

    @Override
    public ExaminationResponseDTO updateExamination(Long id, ExaminationRequestDTO dto, String username) {
        Examination examination = examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with id: " + id));

        // Verify ownership: only the doctor who created the examination can update it
        String examDoctorUsername = examination.getDoctor().getUser().getUsername();
        if (!examDoctorUsername.equals(username)) {
            throw new AccessDeniedException("You are not authorized to update this examination");
        }

        // Update fields
        examination.setTreatment(dto.getTreatment());
        examination.setPrice(dto.getPrice());

        // Update diagnosis if provided
        if (dto.getDiagnosisId() != null) {
            Diagnosis diagnosis = diagnosisRepository.findById(dto.getDiagnosisId())
                    .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with id: " + dto.getDiagnosisId()));
            examination.setDiagnosis(diagnosis);
        } else {
            examination.setDiagnosis(null);
        }

        // Update patient if patientId is provided
        if (dto.getPatientId() != null) {
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));
            examination.setPatient(patient);
            examination.setPaidByNzok(patient.isInsured());
        }

        examination = examinationRepository.save(examination);

        return mapToResponseDTO(examination);
    }

    @Override
    public void deleteExamination(Long id) {
        Examination examination = examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with id: " + id));
        sickLeaveRepository.findByExaminationId(id).ifPresent(sickLeaveRepository::delete);
        examinationRepository.delete(examination);
    }

    private ExaminationResponseDTO mapToResponseDTO(Examination examination) {
        String doctorName = null;
        if (examination.getDoctor() != null) {
            doctorName = examination.getDoctor().getName();
        }

        String patientName = null;
        if (examination.getPatient() != null) {
            patientName = examination.getPatient().getName();
        }

        String diagnosisCode = null;
        String diagnosisName = null;
        if (examination.getDiagnosis() != null) {
            diagnosisCode = examination.getDiagnosis().getCode();
            diagnosisName = examination.getDiagnosis().getName();
        }

        return new ExaminationResponseDTO(
                examination.getId(),
                examination.getExamDate(),
                doctorName,
                patientName,
                diagnosisCode,
                diagnosisName,
                examination.getTreatment(),
                examination.getPrice(),
                examination.isPaidByNzok()
        );
    }
}
