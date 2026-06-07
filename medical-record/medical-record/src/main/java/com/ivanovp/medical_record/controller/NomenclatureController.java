package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.request.DiagnosisRequestDTO;
import com.ivanovp.medical_record.dto.request.SpecialtyRequestDTO;
import com.ivanovp.medical_record.dto.response.DiagnosisResponseDTO;
import com.ivanovp.medical_record.dto.response.SpecialtyResponseDTO;
import com.ivanovp.medical_record.service.NomenclatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NomenclatureController {

    private final NomenclatureService nomenclatureService;

    @GetMapping("/api/specialties")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<SpecialtyResponseDTO>> getAllSpecialties() {
        return ResponseEntity.ok(nomenclatureService.getAllSpecialties());
    }

    @PostMapping("/api/admin/specialties")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecialtyResponseDTO> createSpecialty(@Valid @RequestBody SpecialtyRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nomenclatureService.createSpecialty(dto));
    }

    @PutMapping("/api/admin/specialties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecialtyResponseDTO> updateSpecialty(@PathVariable Long id, @Valid @RequestBody SpecialtyRequestDTO dto) {
        return ResponseEntity.ok(nomenclatureService.updateSpecialty(id, dto));
    }

    @DeleteMapping("/api/admin/specialties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Long id) {
        nomenclatureService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/diagnoses")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<DiagnosisResponseDTO>> getAllDiagnoses() {
        return ResponseEntity.ok(nomenclatureService.getAllDiagnoses());
    }

    @GetMapping("/api/diagnoses/my-specialty")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<DiagnosisResponseDTO>> getMySpecialtyDiagnoses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(nomenclatureService.getMySpecialtyDiagnoses(username));
    }

    @PostMapping("/api/admin/diagnoses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiagnosisResponseDTO> createDiagnosis(@Valid @RequestBody DiagnosisRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nomenclatureService.createDiagnosis(dto));
    }

    @PutMapping("/api/admin/diagnoses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiagnosisResponseDTO> updateDiagnosis(@PathVariable Long id, @Valid @RequestBody DiagnosisRequestDTO dto) {
        return ResponseEntity.ok(nomenclatureService.updateDiagnosis(id, dto));
    }

    @DeleteMapping("/api/admin/diagnoses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDiagnosis(@PathVariable Long id) {
        nomenclatureService.deleteDiagnosis(id);
        return ResponseEntity.noContent().build();
    }
}
