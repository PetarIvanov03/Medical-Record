package com.ivanovp.medical_record.service.impl;

import com.ivanovp.medical_record.dto.response.DiagnosisResponseDTO;
import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;
import com.ivanovp.medical_record.dto.response.RevenueDTO;
import com.ivanovp.medical_record.dto.response.StatCountDTO;
import com.ivanovp.medical_record.repository.DoctorRepository;
import com.ivanovp.medical_record.repository.ExaminationRepository;
import com.ivanovp.medical_record.repository.PatientRepository;
import com.ivanovp.medical_record.repository.SickLeaveRepository;
import com.ivanovp.medical_record.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ExaminationRepository examinationRepository;
    private final PatientRepository patientRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public List<PatientResponseDTO> getPatientsByDiagnosis(Long diagnosisId) {
        return examinationRepository.findPatientsByDiagnosisId(diagnosisId)
                .stream()
                .map(patient -> new PatientResponseDTO(
                        patient.getId(),
                        patient.getName(),
                        patient.getEgn(),
                        patient.isInsured(),
                        patient.getGp() != null ? patient.getGp().getName() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public DiagnosisResponseDTO getMostCommonDiagnosis() {
        com.ivanovp.medical_record.entity.Diagnosis diagnosis = examinationRepository.findMostCommonDiagnosis();
        if (diagnosis == null) {
            return null;
        }
        return new DiagnosisResponseDTO(
                diagnosis.getId(),
                diagnosis.getCode(),
                diagnosis.getName()
        );
    }

    @Override
    public List<PatientResponseDTO> getPatientsByGp(Long gpId) {
        return patientRepository.findByGpId(gpId)
                .stream()
                .map(patient -> new PatientResponseDTO(
                        patient.getId(),
                        patient.getName(),
                        patient.getEgn(),
                        patient.isInsured(),
                        patient.getGp() != null ? patient.getGp().getName() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<StatCountDTO> getGpPatientCounts() {
        return patientRepository.countPatientsByGp()
                .stream()
                .map(row -> new StatCountDTO(
                        (String) row[0],
                        (Long) row[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<StatCountDTO> getDoctorVisitCounts() {
        return examinationRepository.countVisitsPerDoctor()
                .stream()
                .map(row -> new StatCountDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalRevenue() {
        BigDecimal total = examinationRepository.findTotalPatientRevenue();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<RevenueDTO> getRevenueByDoctor() {
        return examinationRepository.findRevenueByDoctor()
                .stream()
                .map(row -> new RevenueDTO(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Integer getPeakSickLeaveMonth() {
        return sickLeaveRepository.findPeakSickLeaveMonth();
    }

    @Override
    public List<StatCountDTO> getDoctorsWithMostSickLeaves() {
        return sickLeaveRepository.findDoctorsWithMostSickLeaves()
                .stream()
                .map(row -> new StatCountDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExaminationResponseDTO> getExaminationsByPeriod(LocalDateTime from, LocalDateTime to) {
        return examinationRepository.findByExamDateBetween(from, to)
                .stream()
                .map(exam -> new ExaminationResponseDTO(
                        exam.getId(),
                        exam.getExamDate(),
                        exam.getDoctor() != null ? exam.getDoctor().getName() : null,
                        exam.getPatient() != null ? exam.getPatient().getName() : null,
                        exam.getDiagnosis() != null ? exam.getDiagnosis().getCode() : null,
                        exam.getDiagnosis() != null ? exam.getDiagnosis().getName() : null,
                        exam.getTreatment(),
                        exam.getPrice(),
                        exam.isPaidByNzok()
                ))
                .collect(Collectors.toList());
    }
}

