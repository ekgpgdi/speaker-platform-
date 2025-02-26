package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.util.DateUtil;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.dto.request.LectureCreateRequest;
import com.dahye.speakerplatform.lectures.dto.response.LectureListResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureResponse;
import com.dahye.speakerplatform.lectures.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;

    @Transactional
    public ResponseCode createLecture(LectureCreateRequest lectureCreateRequest) {
        Lecture lecture = Lecture.builder()
                .lecturer(lectureCreateRequest.getLecturer())
                .location(lectureCreateRequest.getLocation())
                .capacity(lectureCreateRequest.getCapacity())
                .content(lectureCreateRequest.getContent())
                .currentCapacity(0)
                .startTime(DateUtil.parseToLocalDateTime(lectureCreateRequest.getStartTime()))
                .build();
        lectureRepository.save(lecture);

        return ResponseCode.CREATED;
    }

    @Transactional(readOnly = true)
    public LectureListResponse getLectureList(int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());

        Page<Lecture> lectureList = lectureRepository.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));

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
}
