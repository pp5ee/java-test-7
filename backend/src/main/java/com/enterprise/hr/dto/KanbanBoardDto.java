package com.enterprise.hr.dto;

import lombok.Data;
import java.util.List;

@Data
public class KanbanBoardDto {
    private List<TaskDto> todo;
    private List<TaskDto> inProgress;
    private List<TaskDto> inReview;
    private List<TaskDto> done;
}
