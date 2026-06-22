package me.proj.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import me.proj.dtos.CreateAvailabilityRequest;
import me.proj.entities.Availability;
import me.proj.security.AuthorizationService;
import me.proj.services.AvailabilityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
    private static final Logger log = LogManager.getLogger(AvailabilityController.class);
    private final AvailabilityService availabilityService;
    private final AuthorizationService authorizationService;

    public AvailabilityController(
            AvailabilityService availabilityService,
            AuthorizationService authorizationService
    ) {
        this.availabilityService = availabilityService;
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
        return availabilityService.create(request);
    }

    @GetMapping("/common")
    public List<LocalDate> commonDates(
            @RequestParam Long projectId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return availabilityService.findCommonDates(
                projectId,
                from,
                to
        );
    }

    @PostMapping("/toggle")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggle(
            @RequestParam Long projectId,
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {   // добавили для отладки

        log.info("=== TOGGLE REQUEST RECEIVED projectId={}, userId={}, date={}",
                projectId, userId, date);

        // Временный обход CSRF
        log.info("CSRF Header: {}", request.getHeader("X-XSRF-TOKEN"));

        authorizationService.requireOwnProjectTimeline(projectId, userId);
        availabilityService.toggleBusy(userId, date);
    }
}
