package com.dahye.speakerplatform.lectures.repository;

import com.dahye.speakerplatform.lectures.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface LectureRepositoryCustom {
    Page<Lecture> findByStartTimePlusOneDayGreaterThanEqual(LocalDateTime now, Pageable pageable);

    Page<Lecture> findPopularLectures(int periodDays, Pageable pageable);
}
