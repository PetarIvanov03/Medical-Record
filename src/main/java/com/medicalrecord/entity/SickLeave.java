package com.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "sick_leaves")
@Getter
@Setter
public class SickLeave extends BaseEntity {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "num_days", nullable = false)
    private int numDays;

    @OneToOne(optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
}
