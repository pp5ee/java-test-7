package com.enterprise.hr.config;

import com.enterprise.hr.model.Role;
import com.enterprise.hr.model.RoleType;
import com.enterprise.hr.model.User;
import com.enterprise.hr.repository.RoleRepository;
import com.enterprise.hr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initRoles();
        initAdminUser();
    }

    private void initRoles() {
        Arrays.stream(RoleType.values()).forEach(roleType -> {
            if (!roleRepository.existsByName(roleType)) {
                Role role = new Role();
                role.setName(roleType);
                role.setDisplayName(roleType.getDisplayName());
                role.setDescription(roleType.getDescription());
                role.setLevel(roleType.getLevel());
                roleRepository.save(role);
                log.info("Created role: {}", roleType.getDisplayName());
            }
        });
    }

    private void initAdminUser() {
        String adminEmail = "admin@hr-system.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            Role ceoRole = roleRepository.findByName(RoleType.CEO)
                    .orElseThrow(() -> new RuntimeException("CEO role not found"));

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("Admin@123456"));
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRole(ceoRole);
            admin.setEnabled(true);
            admin.setEmailVerified(true);
            userRepository.save(admin);
            log.info("Created default admin user: {}", adminEmail);
        }
    }
}
