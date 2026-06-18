package me.proj.security;

import me.proj.entities.Project;
import me.proj.entities.User;
import me.proj.repos.ProjectRepository;
import me.proj.repos.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    private final CurrentUserService currentUserService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public AuthorizationService(
            CurrentUserService currentUserService,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.currentUserService = currentUserService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public User requireProjectManager() {
        User currentUser = currentUserService.requireCurrentUser();

        if (!currentUser.canManageProjects()) {
            throw new RuntimeException("Only masters or admins can manage projects and members");
        }

        return currentUser;
    }

    public void requireRoleManager() {
        User currentUser = currentUserService.requireCurrentUser();

        if (!currentUser.canManageRoles()) {
            throw new RuntimeException("Only admins can edit user roles");
        }

    }

    public void requireOwnProjectTimeline(Long projectId, Long userId) {
        User currentUser = currentUserService.requireCurrentUser();

        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own timeline");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found")
                );

        if (!userRepository.existsByIdAndProjects(userId, project)) {
            throw new RuntimeException("Join the project before editing your timeline");
        }

    }
}
