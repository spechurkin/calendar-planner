package me.proj.controllers;

import me.proj.dtos.CalendarDay;
import me.proj.services.CalendarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService service;

    public CalendarController(
            CalendarService service
    ) {
        this.service = service;
    }

    @GetMapping
    public List<CalendarDay> month(
            @RequestParam Long projectId,
            @RequestParam int year,
            @RequestParam int month
    ) {

        return service.buildMonth(
                projectId,
                year,
                month
        );
    }

    @GetMapping("/common")
    public List<String> commonDates(
            @RequestParam Long projectId
    ) {
        return service.nearestCommonDates(projectId);
    }
}
