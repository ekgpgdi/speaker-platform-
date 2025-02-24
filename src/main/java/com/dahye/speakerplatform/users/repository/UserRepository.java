package com.dahye.speakerplatform.users.repository;

import com.dahye.speakerplatform.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmployeeNo(String employeeNo);
}
