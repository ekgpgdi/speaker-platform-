package com.dahye.speakerplatform.lectures.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "사번 관련 요청 모델")
@Getter
@Setter
@AllArgsConstructor
public class EmployeeNoRequest {

    @Schema(description = "사번")
    @NotBlank(message = "REQUIRED_EMPLOYEE_NO")
    private String employeeNo;
}
