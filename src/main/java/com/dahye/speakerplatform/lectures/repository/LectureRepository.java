package com.dahye.speakerplatform.lectures.repository;

import com.dahye.speakerplatform.lectures.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long>, LectureRepositoryCustom {
}
