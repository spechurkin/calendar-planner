package me.proj.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import me.proj.dtos.CreateAvailabilityRequest;
import me.proj.entities.Availability;
import me.proj.entities.AvailabilityStatus;
import me.proj.entities.Project;
import me.proj.entities.User;
import me.proj.repos.AvailabilityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AvailabilityService {
    private final AvailabilityRepository repository;
    private final UserService userService;
    private final ProjectService projectService;

    public AvailabilityService(
            AvailabilityRepository repository,
            UserService userService,
            ProjectService projectService
    ) {
        this.repository = repository;
        this.userService = userService;
        this.projectService = projectService;
    }

    public Availability create(
            CreateAvailabilityRequest request
    ) {
        Project project = projectService.getById(
                request.getProjectId()
        );

        User user = userService.getById(
                request.getUserId()
        );

        requireProjectMember(user, project);

        Availability availability = repository
                .findByProjectAndUserAndDate(
                        project,
                        user,
                        request.getDate()
                )
                .orElse(new Availability());

        availability.setProject(project);
        availability.setUser(user);
        availability.setDate(request.getDate());
        availability.setStatus(request.getStatus());

        return repository.save(availability);
    }

    public List<LocalDate> findCommonDates(
            Long projectId,
            LocalDate from,
            LocalDate to
    ) {
        Project project = projectService.getById(projectId);
        List<User> users = project.getUsers();

        List<LocalDate> result = new ArrayList<>();

        for (
                LocalDate date = from;
                !date.isAfter(to);
                date = date.plusDays(1)
        ) {
            boolean allFree = true;

            for (User user : users) {
                Availability availability = repository
                        .findByProjectAndUserAndDate(project, user, date)
                        .orElse(null);
                if (
                        availability != null &&
                                availability.getStatus()
                                        == AvailabilityStatus.BUSY
                ) {
                    allFree = false;
                    break;
                }
            }

            if (allFree) {
                result.add(date);
            }
        }

        return result;
    }

    @Transactional
    public void toggleBusy(Long projectId, Long userId, LocalDate date) {
        log.info("=== TOGGLE START === projectId={}, userId={}, date={}", projectId, userId, date);

        try {
            Project project = projectService.getById(projectId);
            User user = userService.getById(userId);

            if (project == null || user == null) {
                log.error("Project or user not found");
                return;
            }

            requireProjectMember(user, project);

            Optional<Availability> existing = repository.findByProjectAndUserAndDate(project, user, date);

            if (existing.isPresent()) {
                log.info("Deleting BUSY record id={}", existing.get().getId());
                repository.delete(existing.get());
            } else {
                Availability avail = new Availability();
                avail.setProject(project);
                avail.setUser(user);
                avail.setDate(date);
                avail.setStatus(AvailabilityStatus.BUSY);
                repository.save(avail);
                log.info("Created new BUSY record");
            }
            log.info("=== TOGGLE SUCCESS ===");
        } catch (Exception e) {
            log.error("Toggle failed", e);
            throw e; // чтобы увидеть ошибку в ответе
        }
    }

    private void requireProjectMember(User user, Project project) {
        if (!userService.isMemberOfProject(user.getId(), project.getId())) {
            throw new RuntimeException("User is not a project member");
        }
    }
}