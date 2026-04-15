package com.enterprise.hr.dto;

import com.enterprise.hr.model.TaskPriority;
import com.enterprise.hr.model.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UserSummaryDto creator;
    private UserSummaryDto assignee;
    private Long parentTaskId;
    private String parentTaskTitle;
    private List<TaskDto> subTasks;
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    private Double estimatedHours;
    private Double actualHours;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
