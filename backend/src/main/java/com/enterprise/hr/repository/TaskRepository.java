package com.enterprise.hr.repository;

import com.enterprise.hr.model.Task;
import com.enterprise.hr.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    Page<Task> findByCreatorId(Long creatorId, Pageable pageable);

    List<Task> findByParentTaskId(Long parentTaskId);

    @Query("SELECT t FROM Task t WHERE t.parentTask IS NULL")
    Page<Task> findRootTasks(Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId OR t.creator.id = :userId")
    Page<Task> findByUserInvolved(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND " +
           "(:creatorId IS NULL OR t.creator.id = :creatorId)")
    Page<Task> findWithFilters(
            @Param("status") TaskStatus status,
            @Param("assigneeId") Long assigneeId,
            @Param("creatorId") Long creatorId,
            Pageable pageable);

    long countByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId AND t.parentTask IS NULL")
    Page<Task> findRootTasksByAssignee(@Param("userId") Long userId, Pageable pageable);
}
