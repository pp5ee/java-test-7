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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private JavaMailSender mailSender;

    private ValueOperations<String, String> valueOps;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
    }

    @Test
    void testRegisterUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("Test@123456");
        request.setName("Test User");

        ApiResponse<AuthResponse> response = authService.register(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getToken());
        assertEquals("test@example.com", response.getData().getUser().getEmail());
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("duplicate@example.com");
        request.setPassword("Test@123456");
        request.setName("First User");

        authService.register(request);

        ApiResponse<AuthResponse> response = authService.register(request);

        assertFalse(response.isSuccess());
    }

    @Test
    void testLogin_Success() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("login@example.com");
        registerRequest.setPassword("Test@123456");
        registerRequest.setName("Login User");
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("Test@123456");

        ApiResponse<AuthResponse> response = authService.login(loginRequest);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getToken());
    }

    @Test
    void testLogin_WrongPassword() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("wrongpass@example.com");
        registerRequest.setPassword("Correct@123456");
        registerRequest.setName("Wrong Pass User");
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrongpass@example.com");
        loginRequest.setPassword("Wrong@123456");

        ApiResponse<AuthResponse> response = authService.login(loginRequest);

        assertFalse(response.isSuccess());
        assertEquals("Invalid email or password", response.getMessage());
    }

    @Test
    void testLogin_NonExistentUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("Test@123456");

        ApiResponse<AuthResponse> response = authService.login(loginRequest);

        assertFalse(response.isSuccess());
        assertEquals("Invalid email or password", response.getMessage());
    }
}