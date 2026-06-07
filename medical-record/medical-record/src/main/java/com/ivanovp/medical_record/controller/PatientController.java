package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.request.ChangeGpDTO;
import com.ivanovp.medical_record.dto.request.PatientCreateDTO;
import com.ivanovp.medical_record.dto.response.PatientHistoryDTO;
import com.ivanovp.medical_record.dto.response.PatientResponseDTO;
import com.ivanovp.medical_record.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/api/patients")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/api/patients/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/api/patients/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PatientHistoryDTO> getPatientHistory(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientHistory(id));
    }

    @GetMapping("/api/patients/my-profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponseDTO> getMyProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(patientService.getMyProfile(username));
    }

    @GetMapping("/api/patients/my-history")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientHistoryDTO> getMyHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(patientService.getMyHistory(username));
    }

    @PutMapping("/api/patients/my-profile/change-gp")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> changeGp(@Valid @RequestBody ChangeGpDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        patientService.changeGp(username, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/admin/patients")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(dto));
    }

    @DeleteMapping("/api/admin/patients/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
