package com.ivanovp.medical_record.service.impl;

import com.ivanovp.medical_record.dto.request.SickLeaveRequestDTO;
import com.ivanovp.medical_record.dto.response.SickLeaveResponseDTO;
import com.ivanovp.medical_record.entity.Examination;
import com.ivanovp.medical_record.entity.SickLeave;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.SickLeaveRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.SickLeaveService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SickLeaveServiceImpl implements SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;
    private final ExaminationRepository examinationRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public SickLeaveServiceImpl(SickLeaveRepository sickLeaveRepository,
                                ExaminationRepository examinationRepository,
                                UserRepository userRepository,
                                DoctorRepository doctorRepository) {
        this.sickLeaveRepository = sickLeaveRepository;
        this.examinationRepository = examinationRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SickLeaveResponseDTO getSickLeaveById(Long id) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sick leave not found with id: " + id));
        return mapToResponseDTO(sickLeave);
    }

    @Override
    public SickLeaveResponseDTO createSickLeave(SickLeaveRequestDTO dto, String username) {
        if (sickLeaveRepository.existsByExaminationId(dto.getExaminationId())) {
            throw new IllegalArgumentException("Sick leave already exists for examination with id: " + dto.getExaminationId());
        }

        Examination examination = examinationRepository.findById(dto.getExaminationId())
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with id: " + dto.getExaminationId()));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        com.ivanovp.medical_record.entity.Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user: " + username));

        if (!examination.getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("You are not authorized to create a sick leave for this examination");
        }

        SickLeave sickLeave = new SickLeave();
        sickLeave.setStartDate(dto.getStartDate());
        sickLeave.setDurationDays(dto.getDurationDays());
        sickLeave.setExamination(examination);

        sickLeave = sickLeaveRepository.save(sickLeave);

        return mapToResponseDTO(sickLeave);
    }

    @Override
    public SickLeaveResponseDTO updateSickLeave(Long id, SickLeaveRequestDTO dto, String username) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sick leave not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        com.ivanovp.medical_record.entity.Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user: " + username));

        if (!sickLeave.getExamination().getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("You are not authorized to update this sick leave");
        }

        sickLeave.setStartDate(dto.getStartDate());
        sickLeave.setDurationDays(dto.getDurationDays());

        sickLeave = sickLeaveRepository.save(sickLeave);

        return mapToResponseDTO(sickLeave);
    }

    @Override
    public void deleteSickLeave(Long id, String username, boolean isAdmin) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sick leave not found with id: " + id));

        if (!isAdmin) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
            com.ivanovp.medical_record.entity.Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user: " + username));

            if (!sickLeave.getExamination().getDoctor().getId().equals(doctor.getId())) {
                throw new AccessDeniedException("You are not authorized to delete this sick leave");
            }
        }

        sickLeaveRepository.delete(sickLeave);
    }

    private SickLeaveResponseDTO mapToResponseDTO(SickLeave sickLeave) {
        String patientName = null;
        if (sickLeave.getExamination() != null && sickLeave.getExamination().getPatient() != null) {
            patientName = sickLeave.getExamination().getPatient().getName();
        }

        String doctorName = null;
        if (sickLeave.getExamination() != null && sickLeave.getExamination().getDoctor() != null) {
            doctorName = sickLeave.getExamination().getDoctor().getName();
        }

        return new SickLeaveResponseDTO(
                sickLeave.getId(),
                sickLeave.getStartDate(),
                sickLeave.getDurationDays(),
                sickLeave.getExamination() != null ? sickLeave.getExamination().getId() : null,
                patientName,
                doctorName
        );
    }
}
