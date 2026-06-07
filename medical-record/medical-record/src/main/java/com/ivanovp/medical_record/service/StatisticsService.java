package com.ivanovp.medical_record.service;

import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.dto.response.MostCommonDiagnosisDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;
import com.ivanovp.medical_record.dto.response.RevenueDTO;
import com.ivanovp.medical_record.dto.response.StatCountDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {

    List<PatientResponseDTO> getPatientsByDiagnosis(Long diagnosisId);

    MostCommonDiagnosisDTO getMostCommonDiagnosis();

    List<PatientResponseDTO> getPatientsByGp(Long gpId);

    List<StatCountDTO> getGpPatientCounts();

    List<StatCountDTO> getDoctorVisitCounts();

    BigDecimal getTotalRevenue();

    List<RevenueDTO> getRevenueByDoctor();

    Integer getPeakSickLeaveMonth();

    List<StatCountDTO> getDoctorsWithMostSickLeaves();

    List<ExaminationResponseDTO> getExaminationsByPeriod(LocalDateTime from, LocalDateTime to);
}
