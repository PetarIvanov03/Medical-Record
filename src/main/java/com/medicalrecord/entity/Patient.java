package com.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserEntity user;

    @Column(unique = true, nullable = false)
    private String egn;

    @Column(nullable = false)
    private String name;

    @Column(name = "has_insurance", nullable = false)
    private boolean hasInsurance = true;

    @ManyToOne
    @JoinColumn(name = "gp_id")
    private Doctor gp;

    @OneToMany(mappedBy = "patient")
    private List<Exam> exams = new ArrayList<>();
}
