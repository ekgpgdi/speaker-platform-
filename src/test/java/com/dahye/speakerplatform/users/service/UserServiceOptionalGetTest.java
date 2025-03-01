package com.dahye.speakerplatform.users.service;

import com.dahye.speakerplatform.users.entity.User;
import com.dahye.speakerplatform.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceOptionalGetTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .employeeNo("12345")
                .build();
    }

    @Test
    @DisplayName("employee no 를 통한 user 찾기 성공 테스트")
    public void getOptionalByEmployeeNo_Success() {
        // Given
        Mockito.when(userRepository.findByEmployeeNo("12345")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.getOptionalByEmployeeNo("12345");

        // Then
        assertTrue(result.isPresent(), "유저가 존재하지 않습니다.");
        assertEquals("12345", result.get().getEmployeeNo(), "유저 사번이 맞지 않습니다.");
        assertEquals(1L, result.get().getId(), "유저 ID가 맞지 않습니다.");
    }
}
