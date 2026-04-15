package com.enterprise.hr.dto;

import lombok.Data;

@Data
public class UserSummaryDto {
    private Long id;
    private String fullName;
    private String email;
    private String avatar;
    private String roleName;
}
