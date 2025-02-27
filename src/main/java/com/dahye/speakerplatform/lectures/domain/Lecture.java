package com.dahye.speakerplatform.lectures.domain;

import com.dahye.speakerplatform.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lectures")
public class Lecture extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String lecturer;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "current_capacity", nullable = false)
    private int currentCapacity;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}