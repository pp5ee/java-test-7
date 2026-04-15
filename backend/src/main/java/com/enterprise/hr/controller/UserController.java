package com.enterprise.hr.controller;

import com.enterprise.hr.dto.*;
import com.enterprise.hr.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "User Management", description = "Employee CRUD operations and org chart")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns paginated list of all active employees")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<UserDto> users = userService.getAllUsers(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        return ResponseEntity.ok(ApiResponse.success(userService.getCurrentUserProfile()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user profile information")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", userService.updateUser(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CEO', 'CTO', 'CFO', 'COO')")
    @Operation(summary = "Remove user", description = "Soft-delete a user (admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User removed successfully", null));
    }

    @GetMapping("/{managerId}/reports")
    @Operation(summary = "Get direct reports", description = "Returns all direct reports of a manager")
    public ResponseEntity<ApiResponse<List<UserDto>>> getDirectReports(@PathVariable Long managerId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getDirectReports(managerId)));
    }

    @GetMapping("/org-chart")
    @Operation(summary = "Get org chart data", description = "Returns all users for org chart rendering")
    public ResponseEntity<ApiResponse<List<UserDto>>> getOrgChart() {
        return ResponseEntity.ok(ApiResponse.success(userService.getOrgChart()));
    }
}
