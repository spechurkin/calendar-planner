package me.proj.services;

import me.proj.dtos.SignupRequest;
import me.proj.entities.User;
import me.proj.repos.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            ProjectService projectService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(SignupRequest request) {
        if (userRepository.existsByName(request.getName())) {
            throw new RuntimeException("Name already taken");
        }

        if (userRepository.existsByColor(request.getColor())) {
            throw new RuntimeException("Color already taken");
        }

        User user = new User();
        user.setName(request.getName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setColor(request.getColor());

        User saved = userRepository.save(user);
        projectService.addUser(
                projectService.firstOrCreateDefault().getId(),
                saved.getId()
        );

    }
}
