package com.dahye.speakerplatform.users.repository;


import com.dahye.speakerplatform.users.entity.User;
import com.dahye.speakerplatform.users.enums.Role;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @Test
    @DisplayName("Repository - Optional 유저 조회 성공")
    void findByEmployeeNo_Success() {
        user = User.builder()
                .employeeNo("12345")
                .password("****")
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        // When
        Optional<User> result = userRepository.findByEmployeeNo("12345");

        // Then
        assertTrue(result.isPresent(), "유저가 존재하지 않습니다.");
        Assertions.assertEquals("12345", result.get().getEmployeeNo(), "유저 사번이 맞지 않습니다.");
        Assertions.assertEquals(user.getId(), result.get().getId(), "유저 ID가 맞지 않습니다.");
    }

    @Test
    @DisplayName("Repository - Optional 유저 조회 실패")
    void findByEmployeeNo_FailByNotFound() {
        Optional<User> foundUser = userRepository.findByEmployeeNo("00000");

        assertFalse(foundUser.isPresent(), "유저가 존재합니다.");
    }
}
