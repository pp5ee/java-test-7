package com.enterprise.hr.controller;

import com.enterprise.hr.dto.*;
import com.enterprise.hr.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Task Board", description = "Kanban-style task management endpoints")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task", description = "Create a new task or sub-task")
    public ResponseEntity<ApiResponse<TaskDto>> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskDto task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", task));
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Returns paginated root-level tasks")
    public ResponseEntity<ApiResponse<PageResponse<TaskDto>>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(taskService.getAllTasks(pageable)));
    }

    @GetMapping("/my-tasks")
    @Operation(summary = "Get my tasks", description = "Returns tasks assigned to the current user")
    public ResponseEntity<ApiResponse<PageResponse<TaskDto>>> getMyTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(taskService.getMyTasks(pageable)));
    }

    @GetMapping("/assigned-by-me")
    @Operation(summary = "Get tasks assigned by me", description = "Returns tasks created/assigned by the current user")
    public ResponseEntity<ApiResponse<PageResponse<TaskDto>>> getAssignedByMe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(taskService.getAssignedByMe(pageable)));
    }

    @GetMapping("/kanban")
    @Operation(summary = "Get Kanban board", description = "Returns tasks grouped by status for Kanban board")
    public ResponseEntity<ApiResponse<KanbanBoardDto>> getKanbanBoard() {
        return ResponseEntity.ok(ApiResponse.success(taskService.getKanbanBoard()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<ApiResponse<TaskDto>> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskById(id)));
    }

    @GetMapping("/{id}/subtasks")
    @Operation(summary = "Get sub-tasks", description = "Returns all sub-tasks of a parent task")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getSubTasks(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getSubTasks(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Update task details, status or assignee")
    public ResponseEntity<ApiResponse<TaskDto>> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", taskService.updateTask(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
}
