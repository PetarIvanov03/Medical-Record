package com.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "exams")
@Getter
@Setter
public class Exam extends BaseEntity {

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String diagnosis;


    private String treatment;

    @Column(nullable = false)
    private BigDecimal price;

    @OneToOne(mappedBy = "exam", cascade = CascadeType.ALL)
    private SickLeave sickLeave;
}
