package com.enterprise.hr.service;

import com.enterprise.hr.dto.*;
import com.enterprise.hr.model.Role;
import com.enterprise.hr.model.Task;
import com.enterprise.hr.model.User;
import com.enterprise.hr.repository.RoleRepository;
import com.enterprise.hr.repository.TaskRepository;
import com.enterprise.hr.repository.UserRepository;
import com.enterprise.hr.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskDto createTask(CreateTaskRequest request) {
        User creator = securityUtils.getCurrentUser();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setCreator(creator);
        task.setDueDate(request.getDueDate());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setTags(request.getTags());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }

        if (request.getParentTaskId() != null) {
            Task parentTask = taskRepository.findById(request.getParentTaskId())
                    .orElseThrow(() -> new RuntimeException("Parent task not found"));
            task.setParentTask(parentTask);
        }

        Task saved = taskRepository.save(task);
        return taskMapper.toDto(saved);
    }

    public PageResponse<TaskDto> getAllTasks(Pageable pageable) {
        Page<Task> page = taskRepository.findRootTasks(pageable);
        return taskMapper.toPageResponse(page);
    }

    public PageResponse<TaskDto> getMyTasks(Pageable pageable) {
        Long userId = securityUtils.getCurrentUserId();
        Page<Task> page = taskRepository.findByAssigneeId(userId, pageable);
        return taskMapper.toPageResponse(page);
    }

    public PageResponse<TaskDto> getAssignedByMe(Pageable pageable) {
        Long userId = securityUtils.getCurrentUserId();
        Page<Task> page = taskRepository.findByCreatorId(userId, pageable);
        return taskMapper.toPageResponse(page);
    }

    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return taskMapper.toDto(task);
    }

    public List<TaskDto> getSubTasks(Long parentId) {
        return taskRepository.findByParentTaskId(parentId)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDto updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User current = securityUtils.getCurrentUser();
        boolean isCreator = task.getCreator().getId().equals(current.getId());
        boolean isAssignee = task.getAssignee() != null &&
                             task.getAssignee().getId().equals(current.getId());

        if (!isCreator && !isAssignee && !securityUtils.isAdmin()) {
            throw new RuntimeException("Access denied");
        }

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getEstimatedHours() != null) task.setEstimatedHours(request.getEstimatedHours());
        if (request.getActualHours() != null) task.setActualHours(request.getActualHours());
        if (request.getTags() != null) task.setTags(request.getTags());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if (request.getStatus().name().equals("DONE") && task.getCompletedAt() == null) {
                task.setCompletedAt(LocalDateTime.now());
            }
        }

        // Only creator or admin can reassign
        if (request.getAssigneeId() != null && (isCreator || securityUtils.isAdmin())) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }

        Task saved = taskRepository.save(task);
        return taskMapper.toDto(saved);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User current = securityUtils.getCurrentUser();
        if (!task.getCreator().getId().equals(current.getId()) && !securityUtils.isAdmin()) {
            throw new RuntimeException("Access denied");
        }

        taskRepository.delete(task);
    }

    public KanbanBoardDto getKanbanBoard() {
        Long userId = securityUtils.getCurrentUserId();
        KanbanBoardDto board = new KanbanBoardDto();

        Page<Task> allTasks = taskRepository.findByUserInvolved(userId, Pageable.unpaged());
        List<Task> tasks = allTasks.getContent();

        board.setTodo(tasks.stream()
                .filter(t -> t.getStatus().name().equals("TODO"))
                .map(taskMapper::toDto).collect(Collectors.toList()));
        board.setInProgress(tasks.stream()
                .filter(t -> t.getStatus().name().equals("IN_PROGRESS"))
                .map(taskMapper::toDto).collect(Collectors.toList()));
        board.setInReview(tasks.stream()
                .filter(t -> t.getStatus().name().equals("IN_REVIEW"))
                .map(taskMapper::toDto).collect(Collectors.toList()));
        board.setDone(tasks.stream()
                .filter(t -> t.getStatus().name().equals("DONE"))
                .map(taskMapper::toDto).collect(Collectors.toList()));

        return board;
    }
}
