package com.enterprise.hr.service;

import com.enterprise.hr.dto.*;
import com.enterprise.hr.model.Role;
import com.enterprise.hr.model.RoleType;
import com.enterprise.hr.model.User;
import com.enterprise.hr.repository.RoleRepository;
import com.enterprise.hr.repository.UserRepository;
import com.enterprise.hr.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;
    private final UserMapper userMapper;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role defaultRole = roleRepository.findByName(RoleType.EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(defaultRole);
        user.setEnabled(false);
        user.setEmailVerified(false);

        userRepository.save(user);

        // Send verification code
        emailService.generateAndSendVerificationCode(request.getEmail());
        log.info("User registered: {}", request.getEmail());
    }

    @Transactional
    public void sendVerificationCode(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email not found");
        }
        emailService.generateAndSendVerificationCode(email);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        boolean valid = emailService.verifyCode(request.getEmail(), request.getCode());
        if (!valid) {
            throw new RuntimeException("Invalid or expired verification code");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailVerified(true);
        user.setEnabled(true);
        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
        log.info("Email verified for user: {}", request.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (DisabledException e) {
            throw new RuntimeException("Account not activated. Please verify your email.");
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // Update last login
        userRepository.findByEmailAndDeletedFalse(request.getEmail()).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });

        User user = userRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtTokenProvider.getJwtExpiration(),
                userMapper.toDto(user)
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtTokenProvider.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtTokenProvider.validateToken(refreshToken, userDetails)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtTokenProvider.generateToken(userDetails);
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(
                newAccessToken,
                refreshToken,
                jwtTokenProvider.getJwtExpiration(),
                userMapper.toDto(user)
        );
    }
}
