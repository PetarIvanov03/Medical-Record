package com.medicalrecord.service.impl;

import com.medicalrecord.dto.view.ExamViewModel;
import com.medicalrecord.dto.view.PatientViewModel;
import com.medicalrecord.dto.view.ReportItemViewModel;
import com.medicalrecord.repository.DoctorRepository;
import com.medicalrecord.repository.ExamRepository;
import com.medicalrecord.repository.PatientRepository;
import com.medicalrecord.repository.SickLeaveRepository;
import com.medicalrecord.service.ReportService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final PatientRepository patientRepository;
    private final ExamRepository examRepository;
    private final DoctorRepository doctorRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final ModelMapper modelMapper;

    public ReportServiceImpl(PatientRepository patientRepository, ExamRepository examRepository, DoctorRepository doctorRepository, SickLeaveRepository sickLeaveRepository, ModelMapper modelMapper) {
        this.patientRepository = patientRepository;
        this.examRepository = examRepository;
        this.doctorRepository = doctorRepository;
        this.sickLeaveRepository = sickLeaveRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PatientViewModel> getPatientsByDiagnosis(String diagnosis) {
        return patientRepository.findAllByExamsDiagnosis(diagnosis).stream()
                .map(p -> modelMapper.map(p, PatientViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public String getMostCommonDiagnosis() {
        return examRepository.findMostCommonDiagnosis().orElse("N/A");
    }

    @Override
    public List<PatientViewModel> getPatientsByGp(Long gpId) {
        return patientRepository.findAllByGpId(gpId).stream()
                .map(p -> modelMapper.map(p, PatientViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalOutofPocket() {
        return examRepository.calculateTotalOutofPocket().orElse(BigDecimal.ZERO);
    }

    @Override
    public List<ReportItemViewModel> getOutofPocketByDoctor() {
        return examRepository.calculateTotalOutofPocketByDoctor().stream()
                .map(proj -> new ReportItemViewModel(proj.getDoctorName(), proj.getTotalIncome().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportItemViewModel> getPatientCountPerGp() {
        return doctorRepository.countPatientsPerGp().stream()
                .map(proj -> new ReportItemViewModel(proj.getDoctorName(), proj.getPatientCount().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportItemViewModel> getVisitCountPerDoctor() {
        return doctorRepository.countVisitsPerDoctor().stream()
                .map(proj -> new ReportItemViewModel(proj.getDoctorName(), proj.getVisitCount().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamViewModel> getPatientVisitHistory(Long patientId) {
        return examRepository.findAllByPatientIdOrderByDateDesc(patientId).stream()
                .map(e -> modelMapper.map(e, ExamViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamViewModel> getFilteredExams(Long doctorId, LocalDate startDate, LocalDate endDate) {
        return examRepository.findAllByDoctorIdAndDateBetween(doctorId, startDate, endDate).stream()
                .map(e -> modelMapper.map(e, ExamViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public Integer getMonthWithMostSickLeaves() {
        return sickLeaveRepository.findMonthWithMostSickLeaves().orElse(null);
    }

    @Override
    public List<ReportItemViewModel> getDoctorsWithMostSickLeaves() {
        var projections = sickLeaveRepository.findDoctorsWithMostSickLeavesDesc();
        if (projections.isEmpty()) {
            return List.of();
        }

        Long maxCount = projections.get(0).getSickLeaveCount();
        return projections.stream()
                .filter(proj -> proj.getSickLeaveCount().equals(maxCount))
                .map(proj -> new ReportItemViewModel(proj.getDoctorName(), proj.getSickLeaveCount().toString()))
                .collect(Collectors.toList());
    }
}
