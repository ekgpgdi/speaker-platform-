package com.dahye.speakerplatform.users.repository;

import com.dahye.speakerplatform.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
