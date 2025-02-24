package com.dahye.speakerplatform.users.entity;

import com.dahye.speakerplatform.common.domain.BaseTimeEntity;
import com.dahye.speakerplatform.users.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_no", nullable = false, length = 5)
    private String employeeNo;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}