package me.proj.controllers;

import jakarta.validation.Valid;
import me.proj.dtos.CreateAvailabilityRequest;
import me.proj.entities.Availability;
import me.proj.security.AuthorizationService;
import me.proj.services.AvailabilityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
    private static final Logger log = LogManager.getLogger(AvailabilityController.class);
    private final AvailabilityService service;
    private final AuthorizationService authorizationService;

    public AvailabilityController(
            AvailabilityService service,
            AuthorizationService authorizationService
    ) {
        this.service = service;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public Availability create(
            @Valid
            @RequestBody
            CreateAvailabilityRequest request
    ) {
        authorizationService.requireOwnProjectTimeline(
                request.getProjectId(),
                request.getUserId()
        );
        return service.create(request);
    }

    @GetMapping("/common")
    public List<LocalDate> commonDates(
            @RequestParam Long projectId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return service.findCommonDates(
                projectId,
                from,
                to
        );
    }

    @PostMapping("/toggle")
    public void toggle(
            @RequestParam Long projectId,
            @RequestParam Long userId,
            @RequestParam LocalDate date
    ) {
        log.error("toggle: {}, {}, {}", projectId,  userId, date);

        authorizationService.requireOwnProjectTimeline(
                projectId,
                userId
        );

        service.toggleBusy(
                projectId,
                userId,
                date
        );
    }
}
