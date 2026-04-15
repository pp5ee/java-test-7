package com.enterprise.hr.dto;

import com.enterprise.hr.model.RoleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String avatar;
    private String department;
    private String position;
    private RoleDto role;
    private Long managerId;
    private String managerName;
    private boolean enabled;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
