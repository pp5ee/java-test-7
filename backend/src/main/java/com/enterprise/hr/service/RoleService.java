package com.enterprise.hr.service;

import com.enterprise.hr.dto.RoleDto;
import com.enterprise.hr.model.Role;
import com.enterprise.hr.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(a.getLevel(), b.getLevel()))
                .map(userMapper::toRoleDto)
                .collect(Collectors.toList());
    }

    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return userMapper.toRoleDto(role);
    }
}
