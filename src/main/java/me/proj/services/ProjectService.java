package me.proj.services;

import me.proj.dtos.CreateProjectRequest;
import me.proj.entities.Availability;
import me.proj.entities.Project;
import me.proj.entities.User;
import me.proj.repos.AvailabilityRepository;
import me.proj.repos.ProjectRepository;
import me.proj.repos.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            AvailabilityRepository availabilityRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
    }

    public Project create(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());

        return projectRepository.save(project);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Project not found")
                );
    }

    public Project firstOrCreateDefault() {
        return projectRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> projectRepository.save(new Project("Общее мероприятие")));
    }

    public void update(Long id, CreateProjectRequest updated) {
        Project project = getById(id);
        project.setName(updated.getName());
        projectRepository.save(project);
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

        List<Availability> existingMarks = availabilityRepository.findAllByUser(user);

        for (Availability mark : existingMarks) {
            boolean exists = availabilityRepository.existsByProjectAndUserAndDate(project, user, mark.getDate());
            if (!exists) {
                availabilityRepository.save(new Availability(project, user, mark.getDate()));
            }
        }
    }

    @Transactional
    public void removeUser(Long projectId, Long userId) {
        Project project = getById(projectId);
        User user = userRepository.findById(userId).get();

        availabilityRepository.deleteByProjectAndUser(project, user);

        project.getUsers().removeIf(u -> u.getId().equals(userId));
    }

    @Transactional
    public void delete(Long id) {
        if (projectRepository.count() <= 1) {
            return;
        }

        projectRepository.findById(id)
                .ifPresent(projectRepository::delete);
    }
}
