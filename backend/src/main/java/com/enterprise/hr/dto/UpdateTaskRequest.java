package com.enterprise.hr.dto;

import com.enterprise.hr.model.TaskPriority;
import com.enterprise.hr.model.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long assigneeId;
    private LocalDate dueDate;
    private Double estimatedHours;
    private Double actualHours;
    private String tags;
}
