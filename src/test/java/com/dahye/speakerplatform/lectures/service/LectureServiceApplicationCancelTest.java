package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.exception.customException.ApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LectureServiceApplicationCancelTest {
    @InjectMocks
    LectureService lectureService;

    @Mock
    private LectureApplicationService lectureApplicationService;


    @Test
    @DisplayName("강연 신청 취소 성공")
    public void testApplyCancel_Success() {
        Long lectureId = 1L;
        Long applicationId = 1L;

        Mockito.when(lectureApplicationService.cancelApplication(lectureId, applicationId))
                .thenReturn(ResponseCode.SUCCESS);


        ResponseCode result = lectureService.cancel(lectureId, applicationId);

        assertEquals(ResponseCode.SUCCESS, result);
    }

    @Test
    @DisplayName("강연 신청 취소 실패 - 강연 신청 내역 없음")
    public void testApplyCancel_NotFound() {
        Long lectureId = 1L;
        Long applicationId = 1L;

        Mockito.when(lectureApplicationService.cancelApplication(lectureId, applicationId))
                .thenThrow(new ApplicationException(ResponseCode.NOT_FOUND_APPLICATION));

        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> {
            lectureService.cancel(lectureId, applicationId);
        });

        assertEquals(ResponseCode.NOT_FOUND_APPLICATION.toString(), exception.getMessage());
    }
}