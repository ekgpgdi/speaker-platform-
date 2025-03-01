package com.dahye.speakerplatform.lectures.repository;

import com.dahye.speakerplatform.lectures.domain.Lecture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class LectureRepositoryCustomTest {
    @Autowired
    private LectureRepository lectureRepository;

    private Lecture lecture;

    @BeforeEach
    public void setUp() {
        lecture = lectureRepository.save(Lecture.builder()
                .lecturer("홍길동")
                .location("서울 강남")
                .capacity(100)
                .currentCapacity(50)
                .startTime(LocalDateTime.now().plusDays(1))
                .content("AI 기술의 발전과 미래")
                .build());
    }

    @Test
    @DisplayName("Repository - 강의 시작 시간이 현재 시간보다 1일 이상 남은 강의 목록 조회 성공")
    void findByStartTimePlusOneDayGreaterThanEqual_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // When
        Page<Lecture> result = lectureRepository.findByStartTimePlusOneDayGreaterThanEqual(now, pageable);

        // Then
        assertNotNull(result, "결과는 null이어서는 안 됩니다.");
        assertFalse(result.getContent().isEmpty(), "강의 목록이 비어있습니다.");
        assertEquals(1, result.getTotalElements(), "총 강의 수가 맞지 않습니다.");
    }

    @Test
    @DisplayName("Repository - 인기 강의 목록 조회 성공")
    void findPopularLectures_Success() {
        // Given
        int periodDays = 7;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // When
        Page<Lecture> result = lectureRepository.findPopularLectures(periodDays, pageable);

        // Then
        assertNotNull(result, "결과는 null이어서는 안 됩니다.");
        assertFalse(result.getContent().isEmpty(), "인기 강의 목록이 비어있습니다.");
    }

    @Test
    @DisplayName("Repository - 1시간 이상 지난 강의 목록 조회 성공")
    void findLecturesStartedMoreThanOneHourAgo_Success() {
        // Given
        Lecture pastLecture = lectureRepository.save(Lecture.builder()
                .lecturer("김철수")
                .location("서울 종로")
                .capacity(80)
                .currentCapacity(30)
                .startTime(LocalDateTime.now().minusHours(2))
                .content("데이터 분석의 핵심")
                .build());

        // When
        var result = lectureRepository.findLecturesStartedMoreThanOneHourAgo();

        // Then
        assertNotNull(result, "결과는 null이어서는 안 됩니다.");
        assertFalse(result.isEmpty(), "1시간 이상 지난 강의 목록이 비어있습니다.");
        assertTrue(result.contains(pastLecture), "저장된 강의가 결과에 포함되어야 합니다.");
    }

    @AfterEach
    public void tearDown() {
        lectureRepository.deleteAll();
    }
}
