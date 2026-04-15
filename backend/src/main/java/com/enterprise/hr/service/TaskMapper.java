package com.enterprise.hr.service;

import com.enterprise.hr.dto.*;
import com.enterprise.hr.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    private final UserMapper userMapper;

    public TaskMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public TaskDto toDto(Task task) {
        if (task == null) return null;

        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setEstimatedHours(task.getEstimatedHours());
        dto.setActualHours(task.getActualHours());
        dto.setTags(task.getTags());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        dto.setCreator(userMapper.toSummaryDto(task.getCreator()));
        dto.setAssignee(userMapper.toSummaryDto(task.getAssignee()));

        if (task.getParentTask() != null) {
            dto.setParentTaskId(task.getParentTask().getId());
            dto.setParentTaskTitle(task.getParentTask().getTitle());
        }

        if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
            dto.setSubTasks(task.getSubTasks().stream()
                    .map(this::toDtoShallow)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // Shallow DTO to avoid infinite recursion in subtask tree
    public TaskDto toDtoShallow(Task task) {
        if (task == null) return null;

        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCreator(userMapper.toSummaryDto(task.getCreator()));
        dto.setAssignee(userMapper.toSummaryDto(task.getAssignee()));
        return dto;
    }

    public PageResponse<TaskDto> toPageResponse(Page<Task> page) {
        List<TaskDto> content = page.getContent().stream()
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
