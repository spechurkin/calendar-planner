package me.proj.controllers;

import me.proj.entities.Project;
import me.proj.services.CalendarService;
import me.proj.services.ProjectService;
import me.proj.services.UserService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;

@Controller
public class PageController {

    private final UserService userService;
    private final ProjectService projectService;
    private final CalendarService calendarService;

    public PageController(
            UserService userService,
            ProjectService projectService,
            CalendarService calendarService
    ) {
        this.userService = userService;
        this.projectService = projectService;
        this.calendarService = calendarService;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long projectId,
            Model model
    ) {
        LocalDate now =
                LocalDate.now();

        Project activeProject = projectId == null
                ? projectService.firstOrCreateDefault()
                : projectService.getById(projectId);

        YearMonth current =
                YearMonth.of(
                        year != null
                                ? year
                                : now.getYear(),

                        month != null
                                ? month
                                : now.getMonthValue()
                );

        YearMonth previous =
                current.minusMonths(1);

        YearMonth next =
                current.plusMonths(1);

        String monthName = StringUtils.capitalize(
                current.getMonth()
                        .getDisplayName(TextStyle.FULL, LocaleContextHolder.getLocale())
        );

        model.addAttribute(
                "month",
                current.getMonthValue()
        );

        model.addAttribute(
                "year",
                current.getYear()
        );

        model.addAttribute(
                "monthName",
                monthName
        );

        model.addAttribute(
                "previousMonth",
                previous.getMonthValue()
        );

        model.addAttribute(
                "previousYear",
                previous.getYear()
        );

        model.addAttribute(
                "nextMonth",
                next.getMonthValue()
        );

        model.addAttribute(
                "nextYear",
                next.getYear()
        );

        model.addAttribute(
                "activeProject",
                activeProject
        );

        model.addAttribute(
                "projects",
                projectService.findAll()
        );

        model.addAttribute(
                "calendarDays",
                calendarService.buildMonth(
                        activeProject.getId(),
                        current.getYear(),
                        current.getMonthValue()
                )
        );

        model.addAttribute(
                "users",
                userService.findAllByProject(activeProject.getId())
        );

        model.addAttribute(
                "availableUsers",
                userService.findUsersOutsideProject(activeProject.getId())
        );

        model.addAttribute(
                "commonDates",
                calendarService.nearestCommonDates(activeProject.getId())
        );

        return "index";
    }
}
