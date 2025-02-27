package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.exception.customException.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class LectureApplicationServiceApplicationTest {

    @InjectMocks
    private LectureApplicationService lectureApplicationService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private SetOperations<String, String> mockSetOperations;

    @Mock
    private ValueOperations<String, String> mockValueOperations;

    private final Long lectureId = 20L;
    private final String employeeNo = "12345";

    @Test
    @DisplayName("강연 신청 성공")
    public void testApply_Success() {
        // Given
        Mockito.when(redisTemplate.opsForSet()).thenReturn(mockSetOperations);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(mockValueOperations);

        Mockito.when(redisTemplate.hasKey("lecture:" + lectureId + ":startTime")).thenReturn(true);
        Mockito.when(mockValueOperations.get("lecture:" + lectureId + ":startTime"))
                .thenReturn(LocalDateTime.now().plusHours(1).toString());

        Mockito.when(redisTemplate.hasKey("lecture:" + lectureId + ":capacity")).thenReturn(true);
        Mockito.when(mockValueOperations.get("lecture:" + lectureId + ":capacity")).thenReturn("10"); // 좌석 있음

        Mockito.when(mockSetOperations.isMember("lecture:" + lectureId + ":applications:new", employeeNo)).thenReturn(false);

        // When
        ResponseCode responseCode = lectureApplicationService.apply(lectureId, employeeNo);

        // Then
        assertEquals(ResponseCode.CREATED, responseCode);
    }

    @Test
    @DisplayName("강연 신청 실패 - 강연 시작 시간이 지남")
    public void testApply_InvalidLectureTime() {
        // Given
        Mockito.when(redisTemplate.opsForValue()).thenReturn(mockValueOperations);

        Mockito.when(redisTemplate.hasKey("lecture:" + lectureId + ":startTime")).thenReturn(true);
        Mockito.when(mockValueOperations.get("lecture:" + lectureId + ":startTime"))
                .thenReturn(LocalDateTime.now().minusHours(1).toString());

        // When & Then
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            lectureApplicationService.apply(lectureId, employeeNo);
        });

        assertEquals(ResponseCode.INVALID_LECTURE_TIME.toString(), thrown.getMessage());
    }

    @Test
    @DisplayName("강연 신청 실패 - 신청 가능한 자리 없음")
    public void testApply_NoCapacityAvailable() {
        // Given
        Mockito.when(redisTemplate.opsForValue()).thenReturn(mockValueOperations);

        Mockito.when(redisTemplate.hasKey("lecture:" + lectureId + ":startTime")).thenReturn(true);
        Mockito.when(mockValueOperations.get("lecture:" + lectureId + ":startTime"))
                .thenReturn(LocalDateTime.now().plusHours(1).toString());

        Mockito.when(redisTemplate.hasKey("lecture:" + lectureId + ":capacity")).thenReturn(true);
        Mockito.when(mockValueOperations.get("lecture:" + lectureId + ":capacity")).thenReturn("0"); // 좌석 없음

        // When & Then
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            lectureApplicationService.apply(lectureId, employeeNo);
        });

        assertEquals(ResponseCode.NO_CAPACITY_AVAILABLE.toString(), thrown.getMessage());
    }

    @Test
    @DisplayName("강연 신청 실패 - 중복 신청")
    public void testApply_DuplicateApplication() {
        // Given
        Mockito.when(redisTemplate.opsForSet()).thenReturn(mockSetOperations);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(mockValueOperations);

        Mockito.when(redisTemplate.hasKey("lecture:" + lectureId + ":startTime")).thenReturn(true);
        Mockito.when(mockValueOperations.get("lecture:" + lectureId + ":startTime"))
                .thenReturn(LocalDateTime.now().plusHours(1).toString());

        Mockito.when(redisTemplate.hasKey("lecture:" + lectureId + ":capacity")).thenReturn(true);
        Mockito.when(mockValueOperations.get("lecture:" + lectureId + ":capacity")).thenReturn("10"); // 좌석 있음

        Mockito.when(mockSetOperations.isMember("lecture:" + lectureId + ":applications:new", employeeNo)).thenReturn(true);

        // When & Then
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            lectureApplicationService.apply(lectureId, employeeNo);
        });

        assertEquals(ResponseCode.DUPLICATE_APPLICATION.toString(), thrown.getMessage());
    }
}
