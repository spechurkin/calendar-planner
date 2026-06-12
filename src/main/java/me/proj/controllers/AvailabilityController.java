package me.proj.controllers;

import jakarta.validation.Valid;
import me.proj.dtos.CreateAvailabilityRequest;
import me.proj.entities.Availability;
import me.proj.services.AvailabilityService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
    private final AvailabilityService service;

    public AvailabilityController(
            AvailabilityService service
    ) {
        this.service = service;
    }

    @PostMapping
    public Availability create(
            @Valid
            @RequestBody
            CreateAvailabilityRequest request
    ) {
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
        service.toggleBusy(
                projectId,
                userId,
                date
        );
    }
}
