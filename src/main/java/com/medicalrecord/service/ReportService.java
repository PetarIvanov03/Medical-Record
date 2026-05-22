package com.medicalrecord.service;

import com.medicalrecord.dto.view.DoctorViewModel;
import com.medicalrecord.dto.view.ExamViewModel;
import com.medicalrecord.dto.view.PatientViewModel;
import com.medicalrecord.dto.view.ReportItemViewModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<PatientViewModel> getPatientsByDiagnosis(String diagnosis);
    String getMostCommonDiagnosis();
    List<PatientViewModel> getPatientsByGp(Long gpId);
    BigDecimal getTotalOutofPocket();
    List<ReportItemViewModel> getOutofPocketByDoctor();
    List<ReportItemViewModel> getPatientCountPerGp();
    List<ReportItemViewModel> getVisitCountPerDoctor();
    List<ExamViewModel> getPatientVisitHistory(Long patientId);
    List<ExamViewModel> getFilteredExams(Long doctorId, LocalDate startDate, LocalDate endDate);
    Integer getMonthWithMostSickLeaves();
    List<ReportItemViewModel> getDoctorsWithMostSickLeaves();
}
