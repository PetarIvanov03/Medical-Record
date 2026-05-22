package com.medicalrecord.controller;

import com.medicalrecord.dto.binding.PatientBindingModel;
import com.medicalrecord.service.DoctorService;
import com.medicalrecord.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    public PatientController(PatientService patientService, DoctorService doctorService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String getAllPatients(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        return "patients/list";
    }

    @GetMapping("/add")
    public String addPatient(Model model) {
        if (!model.containsAttribute("patientBindingModel")) {
            model.addAttribute("patientBindingModel", new PatientBindingModel());
        }
        model.addAttribute("doctors", doctorService.getAllDoctors().stream().filter(d -> d.isGp()).toList());
        return "patients/add";
    }

    @PostMapping("/add")
    public String addPatientConfirm(@Valid @ModelAttribute PatientBindingModel patientBindingModel,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("patientBindingModel", patientBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.patientBindingModel", bindingResult);
            return "redirect:/patients/add";
        }

        patientService.addPatient(patientBindingModel);
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    public String editPatient(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("patientBindingModel")) {
            PatientBindingModel bm = new PatientBindingModel();
            var pat = patientService.getPatientById(id);
            bm.setId(pat.getId());
            bm.setEgn(pat.getEgn());
            bm.setName(pat.getName());
            bm.setHasInsurance(pat.isHasInsurance());
            bm.setGpId(pat.getGp().getId());
            model.addAttribute("patientBindingModel", bm);
        }
        model.addAttribute("doctors", doctorService.getAllDoctors().stream().filter(d -> d.isGp()).toList());
        return "patients/edit";
    }

    @PostMapping("/{id}/edit")
    public String editPatientConfirm(@PathVariable Long id,
                                    @Valid @ModelAttribute PatientBindingModel patientBindingModel,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("patientBindingModel", patientBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.patientBindingModel", bindingResult);
            return "redirect:/patients/" + id + "/edit";
        }

        patientService.updatePatient(id, patientBindingModel);
        return "redirect:/patients";
    }

    @PostMapping("/{id}/delete")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patients";
    }
}
