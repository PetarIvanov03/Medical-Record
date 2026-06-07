package com.ivanovp.medical_record.service.impl;

import com.ivanovp.medical_record.dto.request.DiagnosisRequestDTO;
import com.ivanovp.medical_record.dto.request.SpecialtyRequestDTO;
import com.ivanovp.medical_record.dto.response.DiagnosisResponseDTO;
import com.ivanovp.medical_record.dto.response.SpecialtyResponseDTO;
import com.ivanovp.medical_record.entity.Diagnosis;
import com.ivanovp.medical_record.entity.Doctor;
import com.ivanovp.medical_record.entity.Specialty;
import com.ivanovp.medical_record.entity.User;
import com.ivanovp.medical_record.exception.ResourceNotFoundException;
import com.ivanovp.medical_record.repository.DiagnosisRepository;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.SpecialtyRepository;
import com.ivanovp.medical_record.repository.UserRepository;
import com.ivanovp.medical_record.service.NomenclatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NomenclatureServiceImpl implements NomenclatureService {

    private final SpecialtyRepository specialtyRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final ExaminationRepository examinationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDTO> getAllSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        return specialties.stream()
                .map(this::toSpecialtyResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosisResponseDTO> getAllDiagnoses() {
        List<Diagnosis> diagnoses = diagnosisRepository.findAll();
        return diagnoses.stream()
                .map(this::toDiagnosisResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosisResponseDTO> getMySpecialtyDiagnoses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user: " + username));
        Specialty specialty = doctor.getSpecialty();
        if (specialty == null || specialty.getDiagnoses() == null) {
            return Collections.emptyList();
        }
        return specialty.getDiagnoses().stream()
                .map(this::toDiagnosisResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public SpecialtyResponseDTO createSpecialty(SpecialtyRequestDTO dto) {
        Specialty specialty = new Specialty();
        specialty.setName(dto.getName());
        Specialty saved = specialtyRepository.save(specialty);
        return toSpecialtyResponseDTO(saved);
    }

    @Override
    @Transactional
    public SpecialtyResponseDTO updateSpecialty(Long id, SpecialtyRequestDTO dto) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        specialty.setName(dto.getName());
        Specialty updated = specialtyRepository.save(specialty);
        return toSpecialtyResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteSpecialty(Long id) {
        specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        if (doctorRepository.existsBySpecialtyId(id)) {
            throw new IllegalArgumentException("Cannot delete specialty assigned to doctors");
        }
        specialtyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public DiagnosisResponseDTO createDiagnosis(DiagnosisRequestDTO dto) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode(dto.getCode());
        diagnosis.setName(dto.getName());
        Diagnosis saved = diagnosisRepository.save(diagnosis);

        if (dto.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + dto.getSpecialtyId()));
            specialty.getDiagnoses().add(saved);
            specialtyRepository.save(specialty);
        }

        return toDiagnosisResponseDTO(saved);
    }

    @Override
    @Transactional
    public DiagnosisResponseDTO updateDiagnosis(Long id, DiagnosisRequestDTO dto) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with id: " + id));
        diagnosis.setCode(dto.getCode());
        diagnosis.setName(dto.getName());

        if (dto.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + dto.getSpecialtyId()));
            if (!specialty.getDiagnoses().contains(diagnosis)) {
                specialty.getDiagnoses().add(diagnosis);
                specialtyRepository.save(specialty);
            }
        }

        Diagnosis updated = diagnosisRepository.save(diagnosis);
        return toDiagnosisResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteDiagnosis(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with id: " + id));

        diagnosis.getSpecialties().forEach(specialty -> {
            specialty.getDiagnoses().remove(diagnosis);
            specialtyRepository.save(specialty);
        });

        examinationRepository.findByDiagnosisId(id).forEach(exam -> {
            exam.setDiagnosis(null);
            examinationRepository.save(exam);
        });

        diagnosisRepository.delete(diagnosis);
    }

    private SpecialtyResponseDTO toSpecialtyResponseDTO(Specialty specialty) {
        return new SpecialtyResponseDTO(
                specialty.getId(),
                specialty.getName()
        );
    }

    private DiagnosisResponseDTO toDiagnosisResponseDTO(Diagnosis diagnosis) {
        String specialtyName = diagnosis.getSpecialties().isEmpty()
                ? null
                : diagnosis.getSpecialties().get(0).getName();
        return new DiagnosisResponseDTO(
                diagnosis.getId(),
                diagnosis.getCode(),
                diagnosis.getName(),
                specialtyName
        );
    }
}
