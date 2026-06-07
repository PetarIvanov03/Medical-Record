package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.response.DiagnosisResponseDTO;
import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;
import com.ivanovp.medical_record.dto.response.RevenueDTO;
import com.ivanovp.medical_record.dto.response.StatCountDTO;
import com.ivanovp.medical_record.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/api/statistics/patients-by-diagnosis")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<PatientResponseDTO>> getPatientsByDiagnosis(@RequestParam Long diagnosisId) {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getPatientsByDiagnosis(diagnosisId));
    }

    @GetMapping("/api/statistics/most-common-diagnosis")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DiagnosisResponseDTO> getMostCommonDiagnosis() {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getMostCommonDiagnosis());
    }

    @GetMapping("/api/statistics/patients-by-gp")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<PatientResponseDTO>> getPatientsByGp(@RequestParam Long gpId) {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getPatientsByGp(gpId));
    }

    @GetMapping("/api/statistics/gp-patient-counts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StatCountDTO>> getGpPatientCounts() {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getGpPatientCounts());
    }

    @GetMapping("/api/statistics/doctor-visit-counts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StatCountDTO>> getDoctorVisitCounts() {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getDoctorVisitCounts());
    }

    @GetMapping("/api/statistics/total-revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getTotalRevenue());
    }

    @GetMapping("/api/statistics/revenue-by-doctor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RevenueDTO>> getRevenueByDoctor() {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getRevenueByDoctor());
    }

    @GetMapping("/api/statistics/peak-sick-leave-month")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> getPeakSickLeaveMonth() {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getPeakSickLeaveMonth());
    }

    @GetMapping("/api/statistics/doctors-with-most-sick-leaves")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StatCountDTO>> getDoctorsWithMostSickLeaves() {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getDoctorsWithMostSickLeaves());
    }

    @GetMapping("/api/statistics/examinations-by-period")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<ExaminationResponseDTO>> getExaminationsByPeriod(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getExaminationsByPeriod(from, to));
    }
}
