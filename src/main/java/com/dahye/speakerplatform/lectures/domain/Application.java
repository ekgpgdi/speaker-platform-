package com.dahye.speakerplatform.lectures.domain;

import com.dahye.speakerplatform.common.domain.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "applications")
public class Application extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_no", nullable = false, length = 5)
    private String employeeNo;

    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;
}
