package com.dahye.speakerplatform.lectures.service.lectureService;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.dto.request.LectureCreateRequest;
import com.dahye.speakerplatform.lectures.repository.LectureRepository;
import com.dahye.speakerplatform.lectures.service.LectureApplicationService;
import com.dahye.speakerplatform.lectures.service.LectureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AdminLectureServiceCreateTest {
    @InjectMocks
    LectureService lectureService;

    @Mock
    LectureRepository lectureRepository;

    @Mock
    LectureApplicationService lectureApplicationService;

    @Test
    @DisplayName("[ADMIN] 강연 생성 성공 테스트")
    public void testLectureCreate_Success() {
        // Given
        LectureCreateRequest lectureCreateRequest = new LectureCreateRequest();
        lectureCreateRequest.setLecturer("박다솔");
        lectureCreateRequest.setLocation("경기 화성");
        lectureCreateRequest.setCapacity(6);
        lectureCreateRequest.setStartTime("2025-06-01 10:00:00");
        lectureCreateRequest.setContent("체어 + 바렐 : 강의실에 오시면 당일 진행 방식(체어 또는 바렐)을 안내해 드립니다.");

        Lecture mockLecture = Lecture.builder()
                .id(1L)
                .lecturer("박다솔")
                .location("경기 화성")
                .capacity(6)
                .startTime(LocalDateTime.parse("2025-06-01T10:00:00"))
                .content("체어 + 바렐 : 강의실에 오시면 당일 진행 방식(체어 또는 바렐)을 안내해 드립니다.")
                .build();

        Mockito.when(lectureRepository.save(Mockito.any(Lecture.class))).thenReturn(mockLecture);

        // When
        ResponseCode responseCode = lectureService.createLecture(lectureCreateRequest);

        // Then
        assertEquals(ResponseCode.CREATED, responseCode);
        Mockito.verify(lectureRepository, Mockito.times(1)).save(Mockito.any(Lecture.class));

        Mockito.verify(lectureApplicationService, Mockito.times(1)).createLectureRedis(mockLecture);
    }
}
