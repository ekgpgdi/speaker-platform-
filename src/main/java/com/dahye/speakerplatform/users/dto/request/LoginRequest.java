package com.dahye.speakerplatform.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "로그인 요청 모델")
@Getter
@Setter
public class LoginRequest {

  @Schema(description = "사용자의 사번")
  @NotBlank(message = "REQUIRED_EMPLOYEE_NO")
  private String employeeNo;

  @Schema(description = "사용자의 비밀번호")
  @NotBlank(message = "REQUIRED_PASSWORD")
  private String password;
}
