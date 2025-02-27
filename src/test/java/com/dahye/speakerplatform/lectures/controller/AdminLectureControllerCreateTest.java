package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.security.service.JwtService;
import com.dahye.speakerplatform.lectures.dto.request.LectureCreateRequest;
import com.dahye.speakerplatform.lectures.service.LectureService;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminLectureController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser("TEST_USER")
public class AdminLectureControllerCreateTest {
    private final String API_PATH = "/admin/api/v1/lectures";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LectureService lectureService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("[ADMIN] 강연 생성 성공 테스트")
    public void createTest() throws Exception {
        // Given
        LectureCreateRequest lectureCreateRequest = new LectureCreateRequest();
        lectureCreateRequest.setLecturer("박다솔");
        lectureCreateRequest.setLocation("경기 화성");
        lectureCreateRequest.setCapacity(6);
        lectureCreateRequest.setStartTime("2025-06-01 10:00:00");
        lectureCreateRequest.setContent("체어 + 바렐 : 강의실에 오시면 당일 진행 방식(체어 또는 바렐)을 안내해 드립니다.");

        Mockito.when(lectureService.createLecture(Mockito.any())).thenReturn(ResponseCode.CREATED);

        // When & Then
        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lectureCreateRequest))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(ResponseCode.CREATED.toString()));
    }
}
