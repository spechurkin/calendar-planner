package me.proj.security;

import me.proj.entities.User;
import me.proj.repos.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof SecurityUser securityUser) {
            return userRepository.findById(securityUser.getId())
                    .orElseThrow(() ->
                            new RuntimeException("User not found")
                    );
        }

        throw new RuntimeException("Not authenticated");
    }
}
