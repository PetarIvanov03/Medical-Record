package com.ivanovp.medical_record.controller;

import com.ivanovp.medical_record.dto.request.SickLeaveRequestDTO;
import com.ivanovp.medical_record.dto.response.SickLeaveResponseDTO;
import com.ivanovp.medical_record.service.SickLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sick-leaves")
public class SickLeaveController {

    private final SickLeaveService sickLeaveService;

    @GetMapping("/{id}")
    public ResponseEntity<SickLeaveResponseDTO> getSickLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(sickLeaveService.getSickLeaveById(id));
    }

    @PostMapping
    public ResponseEntity<SickLeaveResponseDTO> createSickLeave(
            @Valid @RequestBody SickLeaveRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sickLeaveService.createSickLeave(dto, username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SickLeaveResponseDTO> updateSickLeave(
            @PathVariable Long id,
            @Valid @RequestBody SickLeaveRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(sickLeaveService.updateSickLeave(id, dto, username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSickLeave(@PathVariable Long id) {
        sickLeaveService.deleteSickLeave(id);
        return ResponseEntity.noContent().build();
    }
}
