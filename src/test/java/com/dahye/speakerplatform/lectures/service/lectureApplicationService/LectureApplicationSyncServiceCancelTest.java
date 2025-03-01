package com.dahye.speakerplatform.lectures.service.lectureApplicationService;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.exception.customException.ApplicationException;
import com.dahye.speakerplatform.lectures.domain.Application;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.repository.ApplicationRepository;
import com.dahye.speakerplatform.lectures.service.LectureApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class LectureApplicationSyncServiceCancelTest {
    @InjectMocks
    private LectureApplicationService lectureApplicationService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private StringRedisTemplate userApplicationRedisTemplate;

    @Mock
    private SetOperations<String, String> mockSetOperations;

    @Test
    @DisplayName("강연 신청 취소 성공")
    public void testCancelApplication_Success() {
        Long lectureId = 1L;
        Long applicationId = 100L;

        Lecture lecture = Lecture.builder()
                .currentCapacity(10)
                .build();

        Application application = Application.builder()
                .lecture(lecture)
                .build();

        Optional<Application> applicationOptional = Optional.of(application);

        Mockito.when(applicationRepository.findById(applicationId))
                .thenReturn(applicationOptional);
        Mockito.when(userApplicationRedisTemplate.opsForSet()).thenReturn(mockSetOperations);

        Mockito.when(userApplicationRedisTemplate.opsForSet().remove(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(1L);
        Mockito.doNothing().when(applicationRepository).delete(Mockito.any(Application.class));

        // 메서드 실행
        ResponseCode result = lectureApplicationService.cancel(lectureId, applicationId);

        assertEquals(ResponseCode.SUCCESS, result);
        assertEquals(9, application.getLecture().getCurrentCapacity());
    }

    @Test
    @DisplayName("강연 신청 취소 실패 - 강연 신청 내역 없음")
    public void testCancelApplication_NotFound() {
        Long lectureId = 1L;
        Long applicationId = 100L;

        Mockito.when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.empty());
        // When
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            lectureApplicationService.cancel(lectureId, applicationId);
        });

        assertEquals(ResponseCode.NOT_FOUND_APPLICATION.toString(), thrown.getMessage());
    }
}
