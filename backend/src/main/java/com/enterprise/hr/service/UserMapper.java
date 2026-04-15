package com.enterprise.hr.service;

import com.enterprise.hr.dto.*;
import com.enterprise.hr.model.Role;
import com.enterprise.hr.model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setDepartment(user.getDepartment());
        dto.setPosition(user.getPosition());
        dto.setEnabled(user.isEnabled());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());

        if (user.getRole() != null) {
            dto.setRole(toRoleDto(user.getRole()));
        }

        if (user.getManager() != null) {
            dto.setManagerId(user.getManager().getId());
            dto.setManagerName(user.getManager().getFullName());
        }

        return dto;
    }

    public UserSummaryDto toSummaryDto(User user) {
        if (user == null) return null;

        UserSummaryDto dto = new UserSummaryDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getDisplayName());
        }
        return dto;
    }

    public RoleDto toRoleDto(Role role) {
        if (role == null) return null;

        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDisplayName(role.getDisplayName());
        dto.setDescription(role.getDescription());
        dto.setLevel(role.getLevel());
        dto.setCreatedAt(role.getCreatedAt());
        return dto;
    }

    public PageResponse<UserDto> toPageResponse(Page<User> page) {
        List<UserDto> content = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst()
        );
    }
}
