package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.security.service.JwtService;
import com.dahye.speakerplatform.lectures.dto.response.LectureListResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureResponse;
import com.dahye.speakerplatform.lectures.service.LectureService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminLectureController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser("TEST_USER")
public class LectureControllerGetListTest {
    private final String API_PATH = "/api/v1/lectures";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LectureService lectureService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("강연 목록 조회 성공 테스트")
    public void getListTest() throws Exception {
        // Given
        int page = 0;
        int size = 10;
        String sort = "createdAt";
        String direction = "desc";

        List<LectureResponse> lectureList = new ArrayList<>();

        lectureList.add(LectureResponse.builder()
                .id(1L)
                .lecturer("홍길동")
                .location("서울 강남")
                .capacity(100)
                .currentCapacity(50)
                .startTime(LocalDateTime.parse("2025-03-01T10:00:00"))
                .content("AI 기술의 발전과 미래")
                .build());

        lectureList.add(LectureResponse.builder()
                .id(2L)
                .lecturer("김철수")
                .location("서울 종로")
                .capacity(80)
                .currentCapacity(30)
                .startTime(LocalDateTime.parse("2025-03-05T14:00:00"))
                .content("데이터 분석의 핵심")
                .build());

        Mockito.when(lectureService.getLectureList(page, size, sort, direction))
                .thenReturn(LectureListResponse.builder()
                        .lectureList(lectureList)
                        .totalElements(2)
                        .totalPages(1)
                        .isLast(true)
                        .build()
                );

        // When & Then
        mockMvc.perform(get(API_PATH).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.totalElements").value(2))
                .andExpect(jsonPath("$.content.totalPages").value(1))
                .andExpect(jsonPath("$.content.isLast").value(true))
                .andExpect(jsonPath("$.content.lectureList[0].id").value(1))
                .andExpect(jsonPath("$.content.lectureList[0].lecturer").value("홍길동"))
                .andExpect(jsonPath("$.content.lectureList[0].location").value("서울 강남"))
                .andExpect(jsonPath("$.content.lectureList[0].startTime").value("2025-03-01T10:00:00"))
                .andExpect(jsonPath("$.content.lectureList[0].content").value("AI 기술의 발전과 미래"))
                .andExpect(jsonPath("$.content.lectureList[1].id").value(2))
                .andExpect(jsonPath("$.content.lectureList[1].lecturer").value("김철수"))
                .andExpect(jsonPath("$.content.lectureList[1].location").value("서울 종로"))
                .andExpect(jsonPath("$.content.lectureList[1].startTime").value("2025-03-05T14:00:00"))
                .andExpect(jsonPath("$.content.lectureList[1].content").value("데이터 분석의 핵심"));
    }
}
