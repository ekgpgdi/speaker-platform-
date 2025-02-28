package com.dahye.speakerplatform.lectures.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "강연 신청자 정보 응답 모델")
@Getter
@AllArgsConstructor
public class ApplicantUserResponse {

    @Schema(description = "사번")
    private String employeeNo;

    @Schema(description = "신청 시각")
   private LocalDateTime applicationAt;
}
