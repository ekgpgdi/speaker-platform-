package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.security.service.JwtService;
import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserListResponse;
import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserResponse;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;

@WebMvcTest(AdminLectureApplicationController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser("TEST_USER")
public class AdminLectureApplicationControllerGetApplicantListTest {
    private final String API_PATH = "/admin/api/v1/lectures/{lecture_id}/applications";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LectureApplicationService lectureApplicationService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("[ADMIN] 강연 신청자 목록 조회 성공 테스트")
    public void getListTest() throws Exception {
        ApplicantUserListResponse response = ApplicantUserListResponse.builder()
                .totalPages(1)
                .isLast(true)
                .totalElements(4)
                .applicantUserList(Arrays.asList(
                        new ApplicantUserResponse("45678", LocalDateTime.parse("2025-02-27T16:05:46")),
                        new ApplicantUserResponse("34567", LocalDateTime.parse("2025-02-27T16:02:32")),
                        new ApplicantUserResponse("23456", LocalDateTime.parse("2025-02-27T15:55:08")),
                        new ApplicantUserResponse("12345", LocalDateTime.parse("2025-02-27T15:55:08"))
                ))
                .build();

        Long lectureId = 1L;
        int page = 0;
        int size = 10;

        Mockito.when(lectureApplicationService.getLectureApplicantUserList(lectureId, page, size))
                .thenReturn(response);

        mockMvc.perform(get(API_PATH, lectureId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())  // 응답 상태 200 OK 확인
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.content.totalPages").value(1))
                .andExpect(jsonPath("$.content.isLast").value(true))
                .andExpect(jsonPath("$.content.totalElements").value(4))
                .andExpect(jsonPath("$.content.applicantUserList.length()").value(4))
                .andExpect(jsonPath("$.content.applicantUserList[0].employeeNo").value("45678"))
                .andExpect(jsonPath("$.content.applicantUserList[0].applicationAt").value("2025-02-27T16:05:46"));
    }
}
