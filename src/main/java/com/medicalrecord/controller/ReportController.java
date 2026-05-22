package com.medicalrecord.controller;

import com.medicalrecord.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String reportsDashboard() {
        return "reports/dashboard";
    }

    @GetMapping("/patients-by-diagnosis")
    public String getPatientsByDiagnosis(@RequestParam(required = false) String diagnosis, Model model) {
        if (diagnosis != null && !diagnosis.isBlank()) {
            model.addAttribute("patients", reportService.getPatientsByDiagnosis(diagnosis));
        }
        return "reports/patients-by-diagnosis";
    }

    @GetMapping("/most-common-diagnosis")
    public String getMostCommonDiagnosis(Model model) {
        model.addAttribute("diagnosis", reportService.getMostCommonDiagnosis());
        return "reports/most-common-diagnosis";
    }

    @GetMapping("/patients-by-gp")
    public String getPatientsByGp(@RequestParam(required = false) Long gpId, Model model) {
        if (gpId != null) {
            model.addAttribute("patients", reportService.getPatientsByGp(gpId));
        }
        return "reports/patients-by-gp";
    }

    @GetMapping("/total-outofpocket")
    public String getTotalOutofPocket(Model model) {
        model.addAttribute("total", reportService.getTotalOutofPocket());
        return "reports/total-outofpocket";
    }

    @GetMapping("/outofpocket-by-doctor")
    public String getOutofPocketByDoctor(Model model) {
        model.addAttribute("data", reportService.getOutofPocketByDoctor());
        return "reports/outofpocket-by-doctor";
    }

    @GetMapping("/patient-count-per-gp")
    public String getPatientCountPerGp(Model model) {
        model.addAttribute("data", reportService.getPatientCountPerGp());
        return "reports/patient-count-per-gp";
    }

    @GetMapping("/visit-count-per-doctor")
    public String getVisitCountPerDoctor(Model model) {
        model.addAttribute("data", reportService.getVisitCountPerDoctor());
        return "reports/visit-count-per-doctor";
    }

    @GetMapping("/patient-history")
    public String getPatientHistory(@RequestParam(required = false) Long patientId, Model model) {
        if (patientId != null) {
            model.addAttribute("exams", reportService.getPatientVisitHistory(patientId));
        }
        return "reports/patient-history";
    }

    @GetMapping("/filtered-exams")
    public String getFilteredExams(@RequestParam(required = false) Long doctorId,
                                   @RequestParam(required = false) LocalDate startDate,
                                   @RequestParam(required = false) LocalDate endDate,
                                   Model model) {
        if (doctorId != null && startDate != null && endDate != null) {
            model.addAttribute("exams", reportService.getFilteredExams(doctorId, startDate, endDate));
        }
        return "reports/filtered-exams";
    }

    @GetMapping("/month-most-sick-leaves")
    public String getMonthWithMostSickLeaves(Model model) {
        model.addAttribute("month", reportService.getMonthWithMostSickLeaves());
        return "reports/month-most-sick-leaves";
    }

    @GetMapping("/doctors-most-sick-leaves")
    public String getDoctorsWithMostSickLeaves(Model model) {
        model.addAttribute("data", reportService.getDoctorsWithMostSickLeaves());
        return "reports/doctors-most-sick-leaves";
    }
}
