package me.proj.services;

import me.proj.dtos.BusyUserDto;
import me.proj.dtos.CalendarDay;
import me.proj.entities.Availability;
import me.proj.entities.AvailabilityStatus;
import me.proj.entities.Project;
import me.proj.entities.User;
import me.proj.repos.AvailabilityRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {

  private final UserService userService;
  private final ProjectService projectService;
  private final AvailabilityRepository repository;

  public CalendarService(
      UserService userService,
      ProjectService projectService,
      AvailabilityRepository repository
  ) {
    this.userService = userService;
    this.projectService = projectService;
    this.repository = repository;
  }

  public List<CalendarDay> buildMonth(
      Long projectId,
      int year,
      int month
  ) {
    Project project = projectService.getById(projectId);
    YearMonth yearMonth =
        YearMonth.of(year, month);

    LocalDate firstDay =
        yearMonth.atDay(1);

    int shift =
        firstDay
            .getDayOfWeek()
            .getValue() - 1;

    LocalDate calendarStart =
        firstDay.minusDays(shift);

    List<CalendarDay> days =
        new ArrayList<>();

    for (int i = 0; i < 42; i++) {

      LocalDate current =
          calendarStart.plusDays(i);

      days.add(
          new CalendarDay(
              current,
              current.equals(LocalDate.now()),
              current.getMonthValue()
                  == month,
              isFreeForAll(project, current),
              busyUsers(current)
          )
      );
    }

    return days;
  }

  public boolean isFreeForAll(
      Project project,
      LocalDate date
  ) {
    List<User> users =
        userService.findAllByProject(project.getId());

    for (User user : users) {
      Availability availability =
          repository
              .findByUserAndDate(
                  user,
                  date
              )
              .orElse(null);

      if (
          availability != null &&
              availability.getStatus()
                  == AvailabilityStatus.BUSY
      ) {
        return false;
      }
    }

    return true;
  }

  public List<String> nearestCommonDates(Long projectId) {
    Project project = projectService.getById(projectId);
    List<String> result = new ArrayList<>();

    DateTimeFormatter formatter =
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(LocaleContextHolder.getLocale());

    LocalDate now = LocalDate.now();

    for (int i = 0; i < 365; i++) {

      LocalDate date = now.plusDays(i);

      if (isFreeForAll(project, date)) {
        result.add(date.format(formatter));
      }

      if (result.size() >= 10) {
        break;
      }
    }

    return result;
  }

  private List<BusyUserDto> busyUsers(
      LocalDate date
  ) {

    return repository
        .findAllByDate(date)
        .stream()
        .filter(a ->
            a.getStatus()
                == AvailabilityStatus.BUSY
        )
        .map(a ->
            new BusyUserDto(
                a.getUser().getId(),
                a.getUser().getName(),
                a.getUser().getColor()
            )
        )
        .toList();
  }
}
