package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.common.security.service.JwtService;
import com.dahye.speakerplatform.lectures.dto.response.LectureApplicationListResponse;
import com.dahye.speakerplatform.lectures.enums.LectureApplicationSort;
import com.dahye.speakerplatform.lectures.service.LectureApplicationService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LectureApplicationController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser("TEST_USER")
public class LectureControllerApplicationGetListTest {
    private final String API_PATH = "/api/v1/lectures/applications";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LectureApplicationService lectureApplicationService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("강연 신청 목록 조회 성공 테스트")
    void getLectureApplicationListByLectureStartTime_ShouldReturnLectureApplicationListResponse() throws Exception {
        // given
        int page = 0;
        int size = 10;
        String employeeNo = "12345";
        LectureApplicationSort sort = LectureApplicationSort.CREATED_AT;
        SortDirection sortDirection = SortDirection.DESC;


        LectureApplicationListResponse response = LectureApplicationListResponse.builder()
                .lectureApplicationList(new ArrayList<>())
                .build();
        Mockito.when(lectureApplicationService.getLectureApplicationListByLectureStartTime(page, size, employeeNo, sort, sortDirection))
                .thenReturn(response);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PATH)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("employeeNo", employeeNo)
                        .param("sort", sort.name())
                        .param("sortDirection", sortDirection.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content.lectureApplicationList").isArray());

        verify(lectureApplicationService, times(1)).getLectureApplicationListByLectureStartTime(page, size, employeeNo, sort, sortDirection);
    }
}
