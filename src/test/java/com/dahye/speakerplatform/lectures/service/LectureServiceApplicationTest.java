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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LectureServiceApplicationTest {
    @InjectMocks
    LectureService lectureService;

    @Mock
    private LectureApplicationService lectureApplicationService;

    @Test
    @DisplayName("강연 신청 성공")
    public void testApply_Success() {
        // Given
        Long lectureId = 20L;
        String employeeNo = "12345";

        // When
        Mockito.when(lectureApplicationService.isApplicationTimeValid(lectureId)).thenReturn(true);
        Mockito.when(lectureApplicationService.isCapacityAvailable(lectureId)).thenReturn(true);
        Mockito.when(lectureApplicationService.isAlreadyApplied(lectureId, employeeNo)).thenReturn(false);
        Mockito.when(lectureApplicationService.saveApplication(lectureId, employeeNo)).thenReturn(ResponseCode.SUCCESS);

        // Then
        ResponseCode result = lectureService.apply(lectureId, employeeNo);
        assertEquals(ResponseCode.CREATED, result);

        verify(lectureApplicationService).isApplicationTimeValid(lectureId);
        verify(lectureApplicationService).isCapacityAvailable(lectureId);
        verify(lectureApplicationService).isAlreadyApplied(lectureId, employeeNo);
        verify(lectureApplicationService).saveApplication(lectureId, employeeNo);
    }

    @Test
    @DisplayName("강연 신청 실패 - 강연 시작 시간이 지남")
    public void testApply_InvalidLectureTime() {
        // Given
        Long lectureId = 20L;
        String employeeNo = "12345";

        // When
        Mockito.when(lectureApplicationService.isApplicationTimeValid(lectureId)).thenReturn(false);

        // Then
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            lectureService.apply(lectureId, employeeNo);
        });
        assertEquals(ResponseCode.INVALID_LECTURE_TIME.toString(), thrown.getMessage());
    }

    @Test
    @DisplayName("강연 신청 실패 - 신청 가능한 자리 없음")
    public void testApply_NoCapacityAvailable() {
        // Given
        Long lectureId = 20L;
        String employeeNo = "12345";

        // When
        Mockito.when(lectureApplicationService.isApplicationTimeValid(lectureId)).thenReturn(true);
        Mockito.when(lectureApplicationService.isCapacityAvailable(lectureId)).thenReturn(false);

        // Then
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            lectureService.apply(lectureId, employeeNo);
        });
        assertEquals(ResponseCode.NO_CAPACITY_AVAILABLE.toString(), thrown.getMessage());
    }

    @Test
    @DisplayName("강연 신청 실패 - 중복 신청")
    public void testApply_DuplicateApplication() {
        // Given
        Long lectureId = 20L;
        String employeeNo = "12345";

        // When
        Mockito.when(lectureApplicationService.isApplicationTimeValid(lectureId)).thenReturn(true);
        Mockito.when(lectureApplicationService.isCapacityAvailable(lectureId)).thenReturn(true);
        Mockito.when(lectureApplicationService.isAlreadyApplied(lectureId, employeeNo)).thenReturn(true);

        // Then
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            lectureService.apply(lectureId, employeeNo);
        });
        assertEquals(ResponseCode.DUPLICATE_APPLICATION.toString(), thrown.getMessage());
    }
}
