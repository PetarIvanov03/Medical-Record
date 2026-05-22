package com.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
public class Doctor {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserEntity user;

    @Column(unique = true, nullable = false)
    private String uin;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialty;

    @Column(name = "is_gp", nullable = false)
    private boolean gp = false;

    @OneToMany(mappedBy = "gp")
    private List<Patient> registeredPatients = new ArrayList<>();

    @OneToMany(mappedBy = "doctor")
    private List<Exam> conductedExams = new ArrayList<>();
}
