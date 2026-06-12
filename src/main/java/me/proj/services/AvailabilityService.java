package me.proj.services;

import me.proj.dtos.CreateAvailabilityRequest;
import me.proj.entities.Availability;
import me.proj.entities.AvailabilityStatus;
import me.proj.entities.User;
import me.proj.repos.AvailabilityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityService {
    private final AvailabilityRepository repository;
    private final UserService userService;

    public AvailabilityService(
            AvailabilityRepository repository,
            UserService userService
    ) {
        this.repository = repository;
        this.userService = userService;
    }

    public Availability create(
            CreateAvailabilityRequest request
    ) {

        User user = userService.getById(
                request.getUserId()
        );

        Availability availability = repository
                .findByUserAndDate(
                        user,
                        request.getDate()
                )
                .orElse(new Availability());

        availability.setUser(user);
        availability.setDate(request.getDate());
        availability.setStatus(request.getStatus());

        return repository.save(availability);
    }

    public List<LocalDate> findCommonDates(
            LocalDate from,
            LocalDate to
    ) {

        List<User> users = userService.findAll();

        List<LocalDate> result = new ArrayList<>();

        for (
                LocalDate date = from;
                !date.isAfter(to);
                date = date.plusDays(1)
        ) {

            boolean allFree = true;

            for (User user : users) {

                Availability availability = repository
                        .findByUserAndDate(user, date)
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
            Long userId,
            LocalDate date
    ) {

        User user =
                userService.getById(userId);

        if (user == null) {
            return;
        }

        Availability availability =
                repository
                        .findByUserAndDate(
                                user,
                                date
                        )
                        .orElse(null);

        if (availability == null) {

            Availability newAvailability =
                    new Availability();

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
}