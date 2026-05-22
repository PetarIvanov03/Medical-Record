package com.medicalrecord.controller;

import com.medicalrecord.dto.binding.ExamBindingModel;
import com.medicalrecord.service.ExamService;
import com.medicalrecord.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/exams")
public class ExamController {

    private final ExamService examService;
    private final PatientService patientService;

    public ExamController(ExamService examService, PatientService patientService) {
        this.examService = examService;
        this.patientService = patientService;
    }

    @GetMapping
    public String getAllExams(Model model) {
        model.addAttribute("exams", examService.getAllExams());
        return "exams/list";
    }

    @GetMapping("/add")
    public String addExam(Model model) {
        if (!model.containsAttribute("examBindingModel")) {
            model.addAttribute("examBindingModel", new ExamBindingModel());
        }
        model.addAttribute("patients", patientService.getAllPatients());
        return "exams/add";
    }

    @PostMapping("/add")
    public String addExamConfirm(@Valid @ModelAttribute ExamBindingModel examBindingModel,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("examBindingModel", examBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.examBindingModel", bindingResult);
            return "redirect:/exams/add";
        }

        examService.addExam(examBindingModel, principal.getName());
        return "redirect:/exams";
    }

    @GetMapping("/{id}/edit")
    public String editExam(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("examBindingModel")) {
            ExamBindingModel bm = new ExamBindingModel();
            var exam = examService.getExamById(id);
            bm.setId(exam.getId());
            bm.setDate(exam.getDate());
            bm.setDiagnosis(exam.getDiagnosis());
            bm.setTreatment(exam.getTreatment());
            bm.setPrice(exam.getPrice());
            bm.setPatientId(exam.getPatient().getId());
            model.addAttribute("examBindingModel", bm);
        }
        model.addAttribute("patients", patientService.getAllPatients());
        return "exams/edit";
    }

    @PostMapping("/{id}/edit")
    public String editExamConfirm(@PathVariable Long id,
                                  @Valid @ModelAttribute ExamBindingModel examBindingModel,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Principal principal) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("examBindingModel", examBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.examBindingModel", bindingResult);
            return "redirect:/exams/" + id + "/edit";
        }

        examService.updateExam(id, examBindingModel, principal.getName());
        return "redirect:/exams";
    }

    @PostMapping("/{id}/delete")
    public String deleteExam(@PathVariable Long id, Principal principal) {
        examService.deleteExam(id, principal.getName());
        return "redirect:/exams";
    }
}
