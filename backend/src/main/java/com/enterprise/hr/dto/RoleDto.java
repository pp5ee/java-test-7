package com.enterprise.hr.dto;

import com.enterprise.hr.model.RoleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleDto {
    private Long id;
    private RoleType name;
    private String displayName;
    private String description;
    private int level;
    private LocalDateTime createdAt;
}
