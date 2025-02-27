package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.dto.response.LectureListResponse;
import com.dahye.speakerplatform.lectures.enums.LectureSort;
import com.dahye.speakerplatform.lectures.repository.LectureRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class LectureServiceGetListTest {
    @InjectMocks
    LectureService lectureService;

    @Mock
    LectureRepository lectureRepository;

    @Test
    @DisplayName("강연 목록 조회 성공 테스트")
    public void lectureList_Success() {
        // Given
        int page = 0;
        int size = 10;
        LectureSort sort = LectureSort.CREATED_AT;
        SortDirection direction = SortDirection.DESC;

        List<Lecture> lectureList = new ArrayList<>();
        lectureList.add(Lecture.builder()
                .id(2L)
                .lecturer("홍길동")
                .location("서울 강남")
                .capacity(100)
                .currentCapacity(50)
                .startTime(LocalDateTime.parse("2025-03-01T10:00:00"))
                .content("AI 기술의 발전과 미래")
                .build());

        lectureList.add(Lecture.builder()
                .id(1L)
                .lecturer("김철수")
                .location("서울 종로")
                .capacity(80)
                .currentCapacity(30)
                .startTime(LocalDateTime.parse("2025-03-05T14:00:00"))
                .content("데이터 분석의 핵심")
                .build());

        Page<Lecture> lecturePage = new PageImpl<>(lectureList, PageRequest.of(page, size, Sort.by(direction.getDirection(), sort.getFieldName())), lectureList.size());

        Mockito.when(lectureRepository.findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(lecturePage);

        // When
        LectureListResponse response = lectureService.getLectureListByLectureStartTime(page, size, sort, direction);

        // Then
        assertEquals(response.getLectureList().size(), 2);
        assertThat(response.getLectureList().get(0).getId()).isEqualTo(2L);
        assertThat(response.getLectureList().get(0).getLecturer()).isEqualTo("홍길동");
        assertThat(response.getLectureList().get(0).getLocation()).isEqualTo("서울 강남");
        assertThat(response.getLectureList().get(0).getStartTime()).isEqualTo("2025-03-01T10:00:00");
        assertThat(response.getLectureList().get(0).getContent()).isEqualTo("AI 기술의 발전과 미래");

        assertThat(response.getLectureList().get(1).getId()).isEqualTo(1L);
        assertThat(response.getLectureList().get(1).getLecturer()).isEqualTo("김철수");
        assertThat(response.getLectureList().get(1).getLocation()).isEqualTo("서울 종로");
        assertThat(response.getLectureList().get(1).getStartTime()).isEqualTo("2025-03-05T14:00:00");
        assertThat(response.getLectureList().get(1).getContent()).isEqualTo("데이터 분석의 핵심");

        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getIsLast()).isTrue();
    }
}
