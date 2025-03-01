package com.dahye.speakerplatform.users.service;

import com.dahye.speakerplatform.users.entity.User;
import com.dahye.speakerplatform.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> getOptionalByEmployeeNo(String employeeNo) {
        return userRepository.findByEmployeeNo(employeeNo);
    }
}
