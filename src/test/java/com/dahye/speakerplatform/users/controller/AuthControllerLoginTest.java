package com.dahye.speakerplatform.users.controller;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.exception.customException.InvalidException;
import com.dahye.speakerplatform.common.exception.customException.NotFoundException;
import com.dahye.speakerplatform.common.security.service.JwtService;
import com.dahye.speakerplatform.users.dto.request.LoginRequest;
import com.dahye.speakerplatform.users.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser("TEST_USER")
public class AuthControllerLoginTest {
    private final String API_PATH = "/api/v1/auth/login";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginTest() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmployeeNo("12345");
        loginRequest.setPassword("password123");

        String token = "valid-jwt-token";

        Mockito.when(authService.login("12345", "password123")).thenReturn(token);

        // When & Then
        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andDo(print()) // 출력
                .andExpect(status().isOk()) // 상태 코드가 200 OK인지 확인
                .andExpect(jsonPath("$.content").value(token));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 사번 입력")
    public void testLogin_InvalidEmployeeNo() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmployeeNo("12345");
        loginRequest.setPassword("password123");

        // 사번을 못찾는 경우
        Mockito.when(authService.login("12345", "password123"))
                .thenThrow(new NotFoundException(ResponseCode.NOT_FOUND_USER));

        // When & Then
        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND_USER"));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 비밀번호 입력")
    public void testLogin_InvalidPassword() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmployeeNo("12345");
        loginRequest.setPassword("wrongpassword");

        // 비밀번호 불일치
        Mockito.when(authService.login("12345", "wrongpassword"))
                .thenThrow(new InvalidException(ResponseCode.INVALID_PASSWORD));

        // When & Then
        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_PASSWORD"));
    }

}
