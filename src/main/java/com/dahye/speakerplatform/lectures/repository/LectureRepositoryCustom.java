package com.dahye.speakerplatform.lectures.repository;

import com.dahye.speakerplatform.lectures.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface LectureRepositoryCustom {
    Page<Lecture> findByStartTimeBetween(LocalDateTime minusWeeks, LocalDateTime plusDays, Pageable pageable);
}
