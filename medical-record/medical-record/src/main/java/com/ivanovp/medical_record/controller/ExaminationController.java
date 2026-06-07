package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.request.ExaminationRequestDTO;
import com.ivanovp.medical_record.dto.response.ExaminationResponseDTO;
import com.ivanovp.medical_record.service.ExaminationService;
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
public class ExaminationController {

    private final ExaminationService examinationService;

    @GetMapping("/api/examinations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExaminationResponseDTO>> getAllExaminations() {
        return ResponseEntity.ok(examinationService.getAllExaminations());
    }

    @GetMapping("/api/examinations/my-history")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<ExaminationResponseDTO>> getDoctorExaminations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(examinationService.getDoctorExaminations(username));
    }

    @GetMapping("/api/examinations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ExaminationResponseDTO> getExaminationById(@PathVariable Long id) {
        return ResponseEntity.ok(examinationService.getExaminationById(id));
    }

    @PostMapping("/api/examinations")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ExaminationResponseDTO> createExamination(
            @Valid @RequestBody ExaminationRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(examinationService.createExamination(dto, username));
    }

    @PutMapping("/api/examinations/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ExaminationResponseDTO> updateExamination(
            @PathVariable Long id,
            @Valid @RequestBody ExaminationRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(examinationService.updateExamination(id, dto, username));
    }

    @DeleteMapping("/api/admin/examinations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExamination(@PathVariable Long id) {
        examinationService.deleteExamination(id);
        return ResponseEntity.noContent().build();
    }
}
