package com.dahye.speakerplatform.lectures.repository;

import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureApplicationResponse;
import com.dahye.speakerplatform.lectures.enums.LectureApplicationSort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationRepositoryCustom {
    Page<LectureApplicationResponse> getLectureApplicationListByLectureStartTime(int page, int size, String employeeNo, LectureApplicationSort sort, SortDirection sortDirection);

    Page<ApplicantUserResponse> getLectureApplicantUserList(Long lectureId, Pageable pageable);
}
