package com.dahye.speakerplatform.lectures.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "강연 정보 응답 모델")
@Getter
@Setter
@Builder
public class LectureResponse {
    @Schema(description = "강연 ID")
    private Long id;

    @Schema(description = "강연자")
    private String lecturer;

    @Schema(description = "강연장")
    private String location;

    @Schema(description = "신청 가능 인원수")
    private Integer capacity;

    @Schema(description = "현재 신청 인원수")
    private int currentCapacity;

    @Schema(description = "강연 시작 시간")
    private LocalDateTime startTime;

    @Schema(description = "강연 내용")
    private String content;
}
