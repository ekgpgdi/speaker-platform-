package com.dahye.speakerplatform.users.controller;

import com.dahye.speakerplatform.common.dto.response.ServerResponse;
import com.dahye.speakerplatform.users.dto.request.LoginRequest;
import com.dahye.speakerplatform.users.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AUTH")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "사번과 비밀번호로 로그인",
            description = "사용자가 사번과 비밀번호로 로그인합니다. 성공하면 JWT 토큰을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 로그인되어 JWT 토큰 반환",
            content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/login")
    public ServerResponse<String> login(
            @Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {

        if (bindingResult != null && bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        return ServerResponse.successResponse(
                authService.login(loginRequest.getEmployeeNo(), loginRequest.getPassword()));
    }
}
