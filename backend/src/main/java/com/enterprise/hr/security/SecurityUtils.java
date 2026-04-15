package com.enterprise.hr.security;

import com.enterprise.hr.model.User;
import com.enterprise.hr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public boolean isAdmin() {
        User user = getCurrentUser();
        int level = user.getRole().getLevel();
        return level <= 2; // CEO, CTO, CFO, COO
    }

    public boolean isManagerOrAbove() {
        User user = getCurrentUser();
        return user.getRole().getLevel() <= 3;
    }

    public boolean canManageUser(Long targetUserId) {
        User current = getCurrentUser();
        if (isAdmin()) return true;
        User target = userRepository.findById(targetUserId).orElse(null);
        if (target == null) return false;
        // Managers can manage their direct reports
        return target.getManager() != null &&
               target.getManager().getId().equals(current.getId());
    }
}
