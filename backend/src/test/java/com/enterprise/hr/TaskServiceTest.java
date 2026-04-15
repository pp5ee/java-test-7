package com.enterprise.hr;

import com.enterprise.hr.dto.*;
import com.enterprise.hr.model.*;
import com.enterprise.hr.repository.*;
import com.enterprise.hr.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthService authService;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private JavaMailSender mailSender;

    private User manager;
    private User employee;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // Create manager
        RegisterRequest managerReq = new RegisterRequest();
        managerReq.setEmail("manager@example.com");
        managerReq.setPassword("Manager@123456");
        managerReq.setName("Manager User");
        authService.register(managerReq);
        manager = userRepository.findByEmail("manager@example.com").orElseThrow();

        Role ctoRole = new Role();
        ctoRole.setName("CTO");
        ctoRole.setLevel(2);
        ctoRole = roleRepository.save(ctoRole);
        manager.setRole(ctoRole);
        userRepository.save(manager);

        // Create employee
        RegisterRequest employeeReq = new RegisterRequest();
        employeeReq.setEmail("employee@example.com");
        employeeReq.setPassword("Employee@123456");
        employeeReq.setName("Employee User");
        authService.register(employeeReq);
        employee = userRepository.findByEmail("employee@example.com").orElseThrow();

        Role devRole = new Role();
        devRole.setName("Developer");
        devRole.setLevel(5);
        devRole = roleRepository.save(devRole);
        employee.setRole(devRole);
        userRepository.save(employee);
    }

    @Test
    void testCreateTask_Success() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setAssigneeId(employee.getId());
        request.setPriority(TaskPriority.HIGH);

        TaskDto task = taskService.createTask(request);

        assertNotNull(task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals(TaskStatus.TODO, task.getStatus());
    }

    @Test
    void testGetKanbanBoard() {
        // Create some tasks
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Kanban Task");
        request.setDescription("Test");
        request.setAssigneeId(employee.getId());
        request.setPriority(TaskPriority.MEDIUM);
        taskService.createTask(request);

        KanbanBoardDto board = taskService.getKanbanBoard(manager.getId());

        assertNotNull(board);
        assertEquals(1, board.getColumns().size());
    }

    @Test
    void testUpdateTaskStatus() {
        CreateTaskRequest createRequest = new CreateTaskRequest();
        createRequest.setTitle("Status Update Task");
        createRequest.setDescription("Test");
        createRequest.setAssigneeId(employee.getId());
        TaskDto createdTask = taskService.createTask(createRequest, manager.getId());

        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);

        TaskDto updatedTask = taskService.updateTask(createdTask.getId(), updateRequest, manager.getId());

        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    void testGetMyTasks() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("My Task");
        request.setDescription("Test");
        request.setAssigneeId(employee.getId());
        taskService.createTask(request);

        List<TaskDto> myTasks = taskService.getMyTasks(employee.getId());

        assertFalse(myTasks.isEmpty());
    }

    @Test
    void testGetAssignedTasks() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Assigned Task");
        request.setDescription("Test");
        request.setAssigneeId(employee.getId());
        taskService.createTask(request);

        List<TaskDto> assignedTasks = taskService.getAssignedTasks(manager.getId());

        assertFalse(assignedTasks.isEmpty());
    }
}