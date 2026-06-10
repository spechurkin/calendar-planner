package me.proj.controllers;

import me.proj.services.CalendarService;
import me.proj.services.UserService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

@Controller
public class PageController {

  private final UserService userService;
  private final CalendarService calendarService;

  public PageController(
      UserService userService,
      CalendarService calendarService
  ) {
    this.userService = userService;
    this.calendarService = calendarService;
  }

  @GetMapping("/")
  public String index(
      Integer month,
      Integer year,
      Model model
  ) {
    Locale locale = LocaleContextHolder.getLocale();

    LocalDate now =
        LocalDate.now();

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
        "calendarDays",
        calendarService.buildMonth(
            current.getYear(),
            current.getMonthValue()
        )
    );

    model.addAttribute(
        "users",
        userService.findAll()
    );

    model.addAttribute(
        "commonDates",
        calendarService.nearestCommonDates()
    );

    return "index";
  }
}