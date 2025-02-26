package com.dahye.speakerplatform.lectures.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "강의 생성 요청 모델")
@Getter
@Setter
public class LectureCreateRequest {

    @Schema(description = "강연자")
    @NotBlank(message = "REQUIRED_LECTURER")
    @Size(max = 100, message = "MAX_LENGTH_LECTURER_EXCEEDS")
    private String lecturer;

    @Schema(description = "강연 장소")
    @NotBlank(message = "REQUIRED_LOCATION")
    @Size(max = 255, message = "MAX_LENGTH_LOCATION_EXCEEDS")
    private String location;

    @Schema(description = "신청 가능 인원 수")
    @NotNull(message = "REQUIRED_CAPACITY")
    private Integer capacity;

    @Schema(description = "강연 시작 시간")
    @NotNull(message = "REQUIRED_START_TIME")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", message = "INVALID_TIME_FORMAT")
    private String startTime;

    @Schema(description = "강연 내역")
    @NotBlank(message = "REQUIRED_CONTENT")
    private String content;
}
