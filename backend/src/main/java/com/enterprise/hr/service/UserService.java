package com.enterprise.hr.service;

import com.enterprise.hr.dto.*;
import com.enterprise.hr.model.Role;
import com.enterprise.hr.model.User;
import com.enterprise.hr.repository.RoleRepository;
import com.enterprise.hr.repository.UserRepository;
import com.enterprise.hr.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityUtils securityUtils;
    private final UserMapper userMapper;

    public PageResponse<UserDto> getAllUsers(String search, Pageable pageable) {
        Page<User> page;
        if (StringUtils.hasText(search)) {
            page = userRepository.searchUsers(search, pageable);
        } else {
            page = userRepository.findAllByDeletedFalse(pageable);
        }
        return userMapper.toPageResponse(page);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findByIdWithRole(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public UserDto getCurrentUserProfile() {
        User user = securityUtils.getCurrentUser();
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!securityUtils.canManageUser(id) && !securityUtils.getCurrentUserId().equals(id)) {
            throw new RuntimeException("Access denied");
        }

        if (StringUtils.hasText(request.getFirstName())) {
            user.setFirstName(request.getFirstName());
        }
        if (StringUtils.hasText(request.getLastName())) {
            user.setLastName(request.getLastName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getDepartment())) {
            user.setDepartment(request.getDepartment());
        }
        if (StringUtils.hasText(request.getPosition())) {
            user.setPosition(request.getPosition());
        }

        // Only admins can change role and manager
        if (securityUtils.isAdmin()) {
            if (request.getRoleId() != null) {
                Role role = roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                user.setRole(role);
            }
            if (request.getManagerId() != null) {
                User manager = userRepository.findById(request.getManagerId())
                        .orElseThrow(() -> new RuntimeException("Manager not found"));
                user.setManager(manager);
            }
        }

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!securityUtils.isAdmin()) {
            throw new RuntimeException("Access denied: Only admins can remove users");
        }

        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getId().equals(id)) {
            throw new RuntimeException("Cannot delete your own account");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDeleted(true);
        user.setEnabled(false);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(currentUser.getId());
        userRepository.save(user);
        log.info("User {} deleted by admin {}", id, currentUser.getId());
    }

    public List<UserDto> getDirectReports(Long managerId) {
        return userRepository.findAllByManagerIdAndDeletedFalse(managerId)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getOrgChart() {
        // Return all active users for org chart building
        return userRepository.findAllByDeletedFalse(Pageable.unpaged())
                .getContent()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
