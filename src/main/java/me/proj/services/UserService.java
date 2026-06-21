package me.proj.services;

import me.proj.dtos.CreateUserRequest;
import me.proj.dtos.UpdateProfileRequest;
import me.proj.entities.Project;
import me.proj.entities.User;
import me.proj.entities.UserRole;
import me.proj.repos.AvailabilityRepository;
import me.proj.repos.ProjectRepository;
import me.proj.repos.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AvailabilityRepository availabilityRepository;
    private final ProjectService projectService;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            AvailabilityRepository availabilityRepository,
            ProjectService projectService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.availabilityRepository = availabilityRepository;
        this.projectService = projectService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void create(CreateUserRequest request) {
        User user = new User();

        user.setName(request.getName());
        user.setColor(request.getColor());

        User saved = userRepository.save(user);
        projectService.addUser(request.getProjectId(), saved.getId());
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllByProject(Long projectId) {
        return userRepository.findDistinctByProjects(
                projectService.getById(projectId)
        );
    }

    public List<User> findUsersOutsideProject(Long projectId) {
        List<Long> projectUserIds = findAllByProject(projectId)
                .stream()
                .map(User::getId)
                .toList();

        return userRepository.findAll()
                .stream()
                .filter(user -> user.getPasswordHash() != null)
                .filter(user -> !projectUserIds.contains(user.getId()))
                .toList();
    }

    public boolean isMemberOfProject(Long userId, Long projectId) {
        Project project = projectService.getById(projectId);
        return userRepository.existsByIdAndProjects(userId, project);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    public User getByProjectAndId(Long projectId, Long id) {
        Project project = projectService.getById(projectId);

        if (!userRepository.existsByIdAndProjects(id, project)) {
            throw new RuntimeException("User not found in project");
        }

        return getById(id);
    }

    public void update(Long id, CreateUserRequest updated) {
        User user = getById(id);

        user.setName(updated.getName());
        user.setColor(updated.getColor());

        userRepository.save(user);
    }

    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getById(userId);

        if (!user.getName().equals(request.getName()) && userRepository.existsByName(request.getName())) {
            throw new RuntimeException("Имя уже занято");
        }

        if (!user.getColor().equals(request.getColor()) && userRepository.existsByColor(request.getColor())) {
            throw new RuntimeException("Цвет уже занят");
        }

        user.setName(request.getName());
        user.setColor(request.getColor());

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
    }

    @Transactional
    public void updateMasterRole(Long userId, boolean master) {
        User user = getById(userId);

        if (master) {
            user.addRole(UserRole.MASTER);
        } else {
            user.removeRole(UserRole.MASTER);
        }

        userRepository.save(user);
    }

    @Transactional
    public void joinProject(Long userId, Long projectId) {
        projectService.addUser(projectId, userId);
    }

    @Transactional
    public void addToProject(Long projectId, Long userId) {
        projectService.addUser(projectId, userId);
    }

    @Transactional
    public void removeFromProject(Long projectId, Long userId) {
        Project project = projectService.getById(projectId);
        User user = getByProjectAndId(projectId, userId);

        availabilityRepository.deleteAll(
                availabilityRepository.findAllByProjectAndUser(project, user)
        );

        projectService.removeUser(projectId, userId);
    }

    @Transactional
    public void delete(Long id) {
        var user =
                userRepository.findById(id)
                        .orElse(null);

        if (user == null) {
            return;
        }

        for (Project project : projectRepository.findAll()) {
            project.getUsers().removeIf(member -> member.getId().equals(id));
        }

        availabilityRepository.deleteAll(
                availabilityRepository.findAllByUser(user)
        );

        userRepository.delete(user);
    }
}
