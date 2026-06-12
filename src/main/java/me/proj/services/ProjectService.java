package me.proj.services;

import me.proj.dtos.CreateProjectRequest;
import me.proj.entities.Project;
import me.proj.entities.User;
import me.proj.repos.ProjectRepository;
import me.proj.repos.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository repository;
    private final UserRepository userRepository;

    public ProjectService(
            ProjectRepository repository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Project create(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        return repository.save(project);
    }

    public List<Project> findAll() {
        return repository.findAll();
    }

    public Project getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Project not found")
                );
    }

    public Project firstOrCreateDefault() {
        return repository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> repository.save(new Project("Общее мероприятие")));
    }

    public void update(Long id, CreateProjectRequest updated) {
        Project project = getById(id);
        project.setName(updated.getName());
        repository.save(project);
    }

    @Transactional
    public void addUser(Long projectId, Long userId) {
        Project project = getById(projectId);
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        boolean alreadyAdded = project.getUsers()
                .stream()
                .anyMatch(existing -> existing.getId().equals(userId));

        if (!alreadyAdded) {
            project.getUsers().add(user);
        }
    }

    @Transactional
    public void removeUser(Long projectId, Long userId) {
        Project project = getById(projectId);

        project.getUsers()
                .removeIf(user -> user.getId().equals(userId));
    }

    @Transactional
    public void delete(Long id) {
        if (repository.count() <= 1) {
            return;
        }

        repository.findById(id)
                .ifPresent(repository::delete);
    }
}
