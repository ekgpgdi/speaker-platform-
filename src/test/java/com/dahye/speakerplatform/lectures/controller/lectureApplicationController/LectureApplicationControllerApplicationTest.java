package com.dahye.speakerplatform.lectures.controller.lectureApplicationController;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.exception.customException.ApplicationException;
import com.dahye.speakerplatform.common.security.service.JwtService;
import com.dahye.speakerplatform.lectures.controller.LectureApplicationController;
import com.dahye.speakerplatform.lectures.dto.request.EmployeeNoRequest;
import com.dahye.speakerplatform.lectures.service.LectureApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LectureApplicationController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser("TEST_USER")
public class LectureApplicationControllerApplicationTest {
    private final String API_PATH = "/api/v1/lectures/{lectureId}/applications";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LectureApplicationService lectureApplicationService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("강연 신청 성공")
    public void testApply_Success() throws Exception {
        Long lectureId = 20L;
        String employeeNo = "12345";
        EmployeeNoRequest employeeNoRequest = new EmployeeNoRequest(employeeNo);

        Mockito.when(lectureApplicationService.apply(lectureId, employeeNo)).thenReturn(ResponseCode.CREATED);

        mockMvc.perform(post(API_PATH, lectureId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeNoRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(ResponseCode.CREATED.toString()));

        verify(lectureApplicationService).apply(lectureId, employeeNo); // 서비스 메서드 호출 확인
    }

    @Test
    @DisplayName("강연 신청 실패 - 강연 시작 시간이 지남")
    public void testApply_InvalidLectureTime() throws Exception {
        Long lectureId = 20L;
        String employeeNo = "12345";
        EmployeeNoRequest employeeNoRequest = new EmployeeNoRequest(employeeNo);

        Mockito.when(lectureApplicationService.apply(lectureId, employeeNo)).thenThrow(new ApplicationException(ResponseCode.INVALID_LECTURE_TIME));

        mockMvc.perform(post(API_PATH, lectureId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeNoRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_LECTURE_TIME.toString()));
    }

    @Test
    @DisplayName("강연 신청 실패 - 신청 가능한 자리 없음")
    public void testApply_NoCapacityAvailable() throws Exception {
        Long lectureId = 20L;
        String employeeNo = "12345";
        EmployeeNoRequest employeeNoRequest = new EmployeeNoRequest(employeeNo);

        Mockito.when(lectureApplicationService.apply(lectureId, employeeNo)).thenThrow(new ApplicationException(ResponseCode.NO_CAPACITY_AVAILABLE));

        mockMvc.perform(post(API_PATH, lectureId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeNoRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.NO_CAPACITY_AVAILABLE.toString()));
    }

    @Test
    @DisplayName("강연 신청 실패 - 중복 신청")
    public void testApply_DuplicateApplication() throws Exception {
        Long lectureId = 20L;
        String employeeNo = "12345";
        EmployeeNoRequest employeeNoRequest = new EmployeeNoRequest(employeeNo);

        // 중복 신청인 경우
        Mockito.when(lectureApplicationService.apply(lectureId, employeeNo)).thenThrow(new ApplicationException(ResponseCode.DUPLICATE_APPLICATION));

        mockMvc.perform(post(API_PATH, lectureId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeNoRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_APPLICATION.toString()));
    }
}
