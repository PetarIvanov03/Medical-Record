package com.medicalrecord.controller;

import com.medicalrecord.dto.binding.DoctorBindingModel;
import com.medicalrecord.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String getAllDoctors(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "doctors/list";
    }

    @GetMapping("/add")
    public String addDoctor(Model model) {
        if (!model.containsAttribute("doctorBindingModel")) {
            model.addAttribute("doctorBindingModel", new DoctorBindingModel());
        }
        return "doctors/add";
    }

    @PostMapping("/add")
    public String addDoctorConfirm(@Valid @ModelAttribute DoctorBindingModel doctorBindingModel,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("doctorBindingModel", doctorBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.doctorBindingModel", bindingResult);
            return "redirect:/doctors/add";
        }

        doctorService.addDoctor(doctorBindingModel);
        return "redirect:/doctors";
    }

    @GetMapping("/{id}/edit")
    public String editDoctor(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("doctorBindingModel")) {
            DoctorBindingModel bm = new DoctorBindingModel();
            var doc = doctorService.getDoctorById(id);
            bm.setId(doc.getId());
            bm.setUin(doc.getUin());
            bm.setName(doc.getName());
            bm.setSpecialty(doc.getSpecialty());
            bm.setGp(doc.isGp());
            model.addAttribute("doctorBindingModel", bm);
        }
        return "doctors/edit";
    }

    @PostMapping("/{id}/edit")
    public String editDoctorConfirm(@PathVariable Long id,
                                    @Valid @ModelAttribute DoctorBindingModel doctorBindingModel,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("doctorBindingModel", doctorBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.doctorBindingModel", bindingResult);
            return "redirect:/doctors/" + id + "/edit";
        }

        doctorService.updateDoctor(id, doctorBindingModel);
        return "redirect:/doctors";
    }

    @PostMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/doctors";
    }
}
