package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.util.DateUtil;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.dto.request.LectureCreateRequest;
import com.dahye.speakerplatform.lectures.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
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
}
