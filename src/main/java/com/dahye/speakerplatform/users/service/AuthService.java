package com.dahye.speakerplatform.users.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.exception.customException.InvalidException;
import com.dahye.speakerplatform.common.exception.customException.NotFoundException;
import com.dahye.speakerplatform.common.security.service.JwtService;
import com.dahye.speakerplatform.users.entity.User;
import com.dahye.speakerplatform.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public String login(String employeeNo, String password) {
        User user =
                userRepository
                        .findByEmployeeNo(employeeNo)
                        .orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidException(ResponseCode.INVALID_PASSWORD);
        }

        return jwtService.generateJwt(user.getId(), user.getRole());
    }
}
