package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.dto.request.LectureCreateRequest;
import com.dahye.speakerplatform.lectures.repository.LectureRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AdminLectureServiceCreateTest {
    @InjectMocks
    LectureService lectureService;

    @Mock
    LectureRepository lectureRepository;

    @Test
    @DisplayName("[ADMIN] 강연 생성 성공 테스트")
    public void testLectureCreate_Success() {
        // Given
        LectureCreateRequest lectureCreateRequest = new LectureCreateRequest();
        lectureCreateRequest.setLecturer("박다솔");
        lectureCreateRequest.setLocation("경기도 화성시 남여울2길 4");
        lectureCreateRequest.setCapacity(6);
        lectureCreateRequest.setStartTime("2025-06-01 10:00:00");
        lectureCreateRequest.setContent("체어 + 바렐 : 강의실에 오시면 당일 진행 방식(체어 또는 바렐)을 안내해 드립니다.");

        // When
        ResponseCode responseCode = lectureService.createLecture(lectureCreateRequest);

        // Then
        assertEquals(ResponseCode.CREATED, responseCode);
        Mockito.verify(lectureRepository, Mockito.times(1)).save(Mockito.any(Lecture.class));
    }
}
