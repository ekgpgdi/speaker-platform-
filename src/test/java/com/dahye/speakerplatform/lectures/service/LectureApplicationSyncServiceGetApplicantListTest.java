package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserListResponse;
import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LectureApplicationSyncServiceGetApplicantListTest {
    @InjectMocks
    private LectureApplicationService lectureApplicationService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Test
    @DisplayName("[ADMIN] 강연 신청자 목록 조회 성공 테스트")
    public void applicantList_Success() {
        Long lectureId = 1L;
        int page = 0;
        int size = 10;


        Page<ApplicantUserResponse> pageResponse = new PageImpl<>(List.of(
                new ApplicantUserResponse("45678", LocalDateTime.parse("2025-02-27T16:05:46")),
                new ApplicantUserResponse("34567", LocalDateTime.parse("2025-02-27T16:02:32")),
                new ApplicantUserResponse("23456", LocalDateTime.parse("2025-02-27T15:55:08")),
                new ApplicantUserResponse("12345", LocalDateTime.parse("2025-02-27T15:55:08"))
        ), Pageable.ofSize(size), 2);

        Mockito.when(applicationRepository.getLectureApplicantUserList(lectureId, PageRequest.of(page, size)))
                .thenReturn(pageResponse);

        ApplicantUserListResponse result = lectureApplicationService.getLectureApplicantUserList(lectureId, page, size);

        assertNotNull(result);
        assertEquals(4, result.getApplicantUserList().size());
        assertEquals(4, result.getTotalElements());
        assertTrue(result.getIsLast());
        verify(applicationRepository, times(1)).getLectureApplicantUserList(lectureId, PageRequest.of(page, size));
    }
}
