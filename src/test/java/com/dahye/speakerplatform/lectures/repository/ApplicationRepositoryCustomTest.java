package com.dahye.speakerplatform.lectures.repository;

import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.lectures.domain.Application;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureApplicationResponse;
import com.dahye.speakerplatform.lectures.enums.LectureApplicationSort;
import com.dahye.speakerplatform.users.entity.User;
import com.dahye.speakerplatform.users.enums.Role;
import com.dahye.speakerplatform.users.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ApplicationRepositoryCustomTest {
    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureRepository lectureRepository;

    private Lecture lecture;

    @BeforeEach
    public void setUp() {
        User user = userRepository.save(User.builder()
                .employeeNo("12345")
                .password("****")
                .role(Role.ROLE_USER)
                .build());

        lecture = lectureRepository.save(Lecture.builder()
                .lecturer("홍길동")
                .location("서울 강남")
                .capacity(100)
                .currentCapacity(50)
                .startTime(LocalDateTime.now().plusDays(1))
                .content("AI 기술의 발전과 미래")
                .build());

        applicationRepository.save(Application.builder()
                .user(user)
                .lecture(lecture)
                .build());
    }

    @Test
    @DisplayName("Repository - 강의 신청 목록 조회 성공")
    void getLectureApplicationListByLectureStartTime_Success() {
        // Given
        String employeeNo = "12345";
        int page = 0;
        int size = 10;
        LectureApplicationSort sort = LectureApplicationSort.CREATED_AT;
        SortDirection sortDirection = SortDirection.DESC;

        // When
        Page<LectureApplicationResponse> result = applicationRepository.getLectureApplicationListByLectureStartTime(page, size, employeeNo, sort, sortDirection);

        // Then
        assertNotNull(result, "결과는 null이어서는 안 됩니다.");
        assertFalse(result.getContent().isEmpty(), "강의 신청 목록이 비어있습니다.");
        assertEquals(1, result.getTotalElements(), "총 강의 신청 수가 맞지 않습니다.");
    }

    @Test
    @DisplayName("Repository - 강의 신청 목록 조회 실패 (없는 유저)")
    void getLectureApplicationListByLectureStartTime_Fail_By_EmployeeNo_Not_Found() {
        // Given
        String employeeNo = "00000"; // 존재하지 않는 유저
        int page = 0;
        int size = 10;
        LectureApplicationSort sort = LectureApplicationSort.CREATED_AT;
        SortDirection sortDirection = SortDirection.DESC;

        // When
        Page<LectureApplicationResponse> result = applicationRepository.getLectureApplicationListByLectureStartTime(page, size, employeeNo, sort, sortDirection);

        // Then
        assertNotNull(result, "결과는 null이어서는 안 됩니다.");
        assertTrue(result.getContent().isEmpty(), "강의 신청 목록이 비어야 합니다.");
        assertEquals(0, result.getTotalElements(), "총 강의 신청 수가 맞지 않습니다.");
    }

    @Test
    @DisplayName("Repository - 강의 신청자 목록 조회 성공")
    void getLectureApplicantUserList_Success() {
        // Given
        Long lectureId = lecture.getId();
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<ApplicantUserResponse> result = applicationRepository.getLectureApplicantUserList(lectureId, pageable);

        // Then
        assertNotNull(result, "결과는 null이어서는 안 됩니다.");
        assertFalse(result.getContent().isEmpty(), "강의 신청자 목록이 비어있습니다.");
    }

    @Test
    @DisplayName("Repository - 강의 신청자 목록 조회 실패 (없는 강의 ID)")
    void getLectureApplicantUserList_Fail_By_Lecture_Not_Found() {
        // Given
        Long lectureId = 999L;
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<ApplicantUserResponse> result = applicationRepository.getLectureApplicantUserList(lectureId, pageable);

        // Then
        assertNotNull(result, "결과는 null이어서는 안 됩니다.");
        assertTrue(result.getContent().isEmpty(), "강의 신청자 목록이 비어야 합니다.");
    }

    @AfterEach
    public void tearDown() {
        applicationRepository.deleteAll();
        userRepository.deleteAll();
        lectureRepository.deleteAll();
    }
}
