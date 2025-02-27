package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.util.DateUtil;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.dto.request.LectureCreateRequest;
import com.dahye.speakerplatform.lectures.dto.response.LectureListResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureResponse;
import com.dahye.speakerplatform.lectures.enums.LectureSort;
import com.dahye.speakerplatform.lectures.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureApplicationService lectureApplicationService;

    @Transactional
    public ResponseCode createLecture(LectureCreateRequest lectureCreateRequest) {
        Lecture lecture = saveLecture(lectureCreateRequest);
        lectureApplicationService.createLectureRedis(lecture);

        return ResponseCode.CREATED;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Lecture saveLecture(LectureCreateRequest lectureCreateRequest) {
        Lecture lecture = Lecture.builder()
                .lecturer(lectureCreateRequest.getLecturer())
                .location(lectureCreateRequest.getLocation())
                .capacity(lectureCreateRequest.getCapacity())
                .content(lectureCreateRequest.getContent())
                .currentCapacity(0)
                .startTime(DateUtil.parseToLocalDateTime(lectureCreateRequest.getStartTime()))
                .build();
        lecture = lectureRepository.save(lecture);
        return lecture;
    }

    @Transactional(readOnly = true)
    public LectureListResponse makeLectureListResponse(Page<Lecture> lectureList) {
        return LectureListResponse.builder()
                .lectureList(lectureList.stream().map(lecture -> LectureResponse.builder()
                        .id(lecture.getId())
                        .lecturer(lecture.getLecturer())
                        .location(lecture.getLocation())
                        .capacity(lecture.getCapacity())
                        .currentCapacity(lecture.getCurrentCapacity())
                        .startTime(lecture.getStartTime())
                        .content(lecture.getContent())
                        .build()).toList())
                .totalPages(lectureList.getTotalPages())
                .totalElements(lectureList.getTotalElements())
                .isLast(lectureList.isLast())
                .build();
    }

    public Sort getSort(LectureSort sort, SortDirection direction) {
        return Sort.by(direction.getDirection(), sort.getFieldName());
    }

    @Transactional(readOnly = true)
    public LectureListResponse getLectureList(int page, int size, LectureSort sort, SortDirection sortDirection) {
        Page<Lecture> lectureList = lectureRepository.findAll(PageRequest.of(page, size, getSort(sort, sortDirection)));

        return makeLectureListResponse(lectureList);
    }

    @Transactional(readOnly = true)
    public LectureListResponse getLectureListByLectureStartTime(int page, int size, LectureSort sort, SortDirection sortDirection) {
        // 강연 시작 시간 + 1일 >= 현재 시간
        Page<Lecture> lectureList = lectureRepository.findByStartTimePlusOneDayGreaterThanEqual(
                LocalDateTime.now(),
                PageRequest.of(page, size, getSort(sort, sortDirection))
        );

        return makeLectureListResponse(lectureList);
    }
}
