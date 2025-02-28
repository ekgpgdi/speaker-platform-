package com.dahye.speakerplatform.lectures.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "신청 강연 정보 응답 모델")
@Getter
@Setter
public class LectureApplicationResponse extends LectureResponse {

    @Schema(description = "신청 ID")
    private Long applicationId;

    public LectureApplicationResponse(Long id, String lecturer, String location, Integer capacity, int currentCapacity, LocalDateTime startTime, String content, Long applicationId) {
        super(id, lecturer, location, capacity, currentCapacity, startTime, content);
        this.applicationId = applicationId;
    }
}
