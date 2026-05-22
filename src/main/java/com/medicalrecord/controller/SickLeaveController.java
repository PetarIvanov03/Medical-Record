package com.medicalrecord.controller;

import com.medicalrecord.dto.binding.SickLeaveBindingModel;
import com.medicalrecord.service.ExamService;
import com.medicalrecord.service.SickLeaveService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sick-leaves")
public class SickLeaveController {

    private final SickLeaveService sickLeaveService;
    private final ExamService examService;

    public SickLeaveController(SickLeaveService sickLeaveService, ExamService examService) {
        this.sickLeaveService = sickLeaveService;
        this.examService = examService;
    }

    @GetMapping
    public String getAllSickLeaves(Model model) {
        model.addAttribute("sickLeaves", sickLeaveService.getAllSickLeaves());
        return "sickleaves/list";
    }

    @GetMapping("/add")
    public String addSickLeave(Model model) {
        if (!model.containsAttribute("sickLeaveBindingModel")) {
            model.addAttribute("sickLeaveBindingModel", new SickLeaveBindingModel());
        }
        model.addAttribute("exams", examService.getAllExams());
        return "sickleaves/add";
    }

    @PostMapping("/add")
    public String addSickLeaveConfirm(@Valid @ModelAttribute SickLeaveBindingModel sickLeaveBindingModel,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("sickLeaveBindingModel", sickLeaveBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.sickLeaveBindingModel", bindingResult);
            return "redirect:/sick-leaves/add";
        }

        sickLeaveService.issueSickLeave(sickLeaveBindingModel);
        return "redirect:/sick-leaves";
    }

    @PostMapping("/{id}/delete")
    public String deleteSickLeave(@PathVariable Long id) {
        sickLeaveService.deleteSickLeave(id);
        return "redirect:/sick-leaves";
    }
}
