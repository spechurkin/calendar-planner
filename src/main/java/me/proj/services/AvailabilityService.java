package me.proj.services;

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

    public void toggleBusy(
            Long projectId,
            Long userId,
            LocalDate date
    ) {
        Project project = projectService.getById(projectId);
        User user = userService.getById(userId);

        if (project == null) {
            return;
        }

        if (user == null) {
            return;
        }

        requireProjectMember(user, project);

        Availability availability =
                repository
                        .findByProjectAndUserAndDate(
                                project,
                                user,
                                date
                        )
                        .orElse(null);

        if (availability == null) {
            Availability newAvailability =
                    new Availability();

            newAvailability.setProject(project);
            newAvailability.setUser(user);
            newAvailability.setDate(date);
            newAvailability.setStatus(
                    AvailabilityStatus.BUSY
            );

            repository.save(newAvailability);

            return;
        }

        repository.delete(availability);
    }

    private void requireProjectMember(User user, Project project) {
        if (!userService.isMemberOfProject(user.getId(), project.getId())) {
            throw new RuntimeException("User is not a project member");
        }
    }
}