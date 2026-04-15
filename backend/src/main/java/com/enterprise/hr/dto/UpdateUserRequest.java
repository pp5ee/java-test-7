package com.enterprise.hr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Size(min = 2, max = 100)
    private String firstName;

    @Size(min = 2, max = 100)
    private String lastName;

    @Size(max = 20)
    private String phone;

    @Size(max = 100)
    private String department;

    @Size(max = 100)
    private String position;

    private Long managerId;
    private Long roleId;
}
