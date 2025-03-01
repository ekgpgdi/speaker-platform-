package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.lectures.dto.response.LectureApplicationListResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureApplicationResponse;
import com.dahye.speakerplatform.lectures.enums.LectureApplicationSort;
import com.dahye.speakerplatform.lectures.repository.ApplicationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LectureApplicationSyncServiceGetListTest {
    @InjectMocks
    private LectureApplicationService lectureApplicationService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Test
    @DisplayName("강연 신청 목록 조회 성공 테스트")
    void getLectureApplicationListByLectureStartTime_ShouldReturnLectureApplicationListResponse() {
        // given
        int page = 0;
        int size = 10;
        String employeeNo = "12345";
        LectureApplicationSort sort = LectureApplicationSort.CREATED_AT;
        SortDirection sortDirection = SortDirection.DESC;

        LectureApplicationResponse response1 = new LectureApplicationResponse(1L, "Lecture 1", "Location 1", 0, 0, LocalDateTime.now(), "Content 1", 1L);
        LectureApplicationResponse response2 = new LectureApplicationResponse(2L, "Lecture 2", "Location 2", 0, 0, LocalDateTime.now(), "Content 2", 2L);
        Page<LectureApplicationResponse> pageResponse = new PageImpl<>(List.of(response1, response2), Pageable.ofSize(size), 2);

        // mocking
        Mockito.when(applicationRepository.getLectureApplicationListByLectureStartTime(page, size, employeeNo, sort, sortDirection))
                .thenReturn(pageResponse);

        // when
        LectureApplicationListResponse result = lectureApplicationService.getLectureApplicationListByLectureStartTime(page, size, employeeNo, sort, sortDirection);

        // then
        assertNotNull(result);
        assertEquals(2, result.getLectureApplicationList().size()); // 응답 리스트 사이즈가 2여야 한다.
        assertEquals(2, result.getTotalElements()); // 전체 강연 신청 수가 2여야 한다.
        assertTrue(result.getIsLast()); // 마지막 페이지 여부
        verify(applicationRepository, times(1)).getLectureApplicationListByLectureStartTime(page, size, employeeNo, sort, sortDirection); // repository 호출 여부 확인
    }
}
