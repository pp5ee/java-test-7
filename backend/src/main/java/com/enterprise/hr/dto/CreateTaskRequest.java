package com.enterprise.hr.dto;

import com.enterprise.hr.model.TaskPriority;
import com.enterprise.hr.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(max = 255)
    private String title;

    private String description;
    private TaskStatus status = TaskStatus.TODO;
    private TaskPriority priority = TaskPriority.MEDIUM;
    private Long assigneeId;
    private Long parentTaskId;
    private LocalDate dueDate;
    private Double estimatedHours;
    private String tags;
}
