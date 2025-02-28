package com.dahye.speakerplatform.users.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.security.service.JwtService;
import com.dahye.speakerplatform.users.entity.User;
import com.dahye.speakerplatform.users.enums.Role;
import com.dahye.speakerplatform.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AuthServiceLoginTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    @DisplayName("로그인 성공 테스트")
    public void testLogin_Success() {
        // Given
        String employeeNo = "12345";
        String password = "password123";
        User user = User.builder()
                .id(1L)
                .password(password)
                .role(Role.ROLE_USER)
                .build();

        Mockito.when(userRepository.findByEmployeeNo(employeeNo)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        Mockito.when(jwtService.generateJwt(user.getId(), user.getRole())).thenReturn("valid-jwt-token");

        // When
        String result = authService.login(employeeNo, password);

        // Then
        assertEquals("valid-jwt-token", result);
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 사번 입력")
    public void testLogin_UserNotFound() {
        // Given
        String employeeNo = "12345";
        String password = "password123";

        // Mocking repository에서 사용자 없음
        Mockito.when(userRepository.findByEmployeeNo(employeeNo)).thenReturn(Optional.empty());

        // When
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            authService.login(employeeNo, password);
        });

        // Then
        assertEquals(ResponseCode.NOT_FOUND_USER.toString(), exception.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 비밀번호 입력")
    public void testLogin_InvalidPassword() {
        // Given
        String employeeNo = "12345";
        String password = "wrongpassword";
        User user = User.builder()
                .id(1L)
                .password("correctpassword")
                .role(Role.ROLE_USER)
                .build();

        // Mocking repository와 passwordEncoder
        Mockito.when(userRepository.findByEmployeeNo(employeeNo)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // When
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            authService.login(employeeNo, password);
        });

        // Then
        assertEquals(ResponseCode.INVALID_PASSWORD.toString(), exception.getMessage());
    }
}
