package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.exception.customException.ApplicationException;
import com.dahye.speakerplatform.common.security.service.JwtService;
import com.dahye.speakerplatform.lectures.service.LectureApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LectureApplicationController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser("TEST_USER")
public class LectureApplicationControllerApplicationCancelTest {
    private final String API_PATH = "/api/v1/lectures/{lectureId}/applications/{applicationId}";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LectureApplicationService lectureApplicationService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("강연 신청 취소 성공")
    public void testApplicationCancel_Success() throws Exception {
        Long lectureId = 1L;
        Long applicationId = 1L;

        Mockito.when(lectureApplicationService.cancel(lectureId, applicationId)).thenReturn(ResponseCode.SUCCESS);

        mockMvc.perform(delete(API_PATH, lectureId, applicationId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(ResponseCode.SUCCESS.toString()));
    }

    @Test
    @DisplayName("강연 신청 취소 실패 - 신청 없음")
    public void testApplicationCancel_NotFound() throws Exception {
        Long lectureId = 1L;
        Long applicationId = 1L;

        Mockito.when(lectureApplicationService.cancel(lectureId, applicationId)).thenThrow(new ApplicationException(ResponseCode.NOT_FOUND_APPLICATION));

        mockMvc.perform(delete(API_PATH, lectureId, applicationId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.NOT_FOUND_APPLICATION.toString()));
    }
}
