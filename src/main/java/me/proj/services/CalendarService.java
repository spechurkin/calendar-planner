package me.proj.services;

import jakarta.transaction.Transactional;
import me.proj.dtos.BusyUserDto;
import me.proj.dtos.CalendarDay;
import me.proj.entities.Availability;
import me.proj.entities.AvailabilityStatus;
import me.proj.entities.Project;
import me.proj.repos.AvailabilityRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarService {
    private final ProjectService projectService;
    private final AvailabilityRepository availabilityRepository;

    public CalendarService(
            ProjectService projectService,
            AvailabilityRepository availabilityRepository
    ) {
        this.projectService = projectService;
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
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
        List<LocalDate> allDates = new ArrayList<>();

        for (int i = 0; i < 42; i++) {
            allDates.add(calendarStart.plusDays(i));
        }

        List<Availability> allAvailabilities =
                availabilityRepository.findAllByProjectAndDateIn(project, allDates);

        Map<LocalDate, List<Availability>> availByDate = allAvailabilities.stream()
                .collect(Collectors.groupingBy(Availability::getDate));

        for (int i = 0; i < 42; i++) {
            LocalDate current = calendarStart.plusDays(i);
            List<Availability> dayAvail = availByDate.getOrDefault(current, List.of());

            days.add(new CalendarDay(
                    current,
                    current.equals(LocalDate.now()),
                    current.getMonthValue() == month,
                    isFreeForAll(dayAvail),
                    busyUsers(dayAvail)
            ));
        }
        return days;
    }

    private boolean isFreeForAll(List<Availability> availabilities) {
        return availabilities.stream()
                .noneMatch(a -> a.getStatus() == AvailabilityStatus.BUSY);
    }

    @Transactional
    public List<String> nearestCommonDates(Long projectId) {
        Project project = projectService.getById(projectId);
        List<String> result = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(LocaleContextHolder.getLocale());

        LocalDate now = LocalDate.now();
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 365; i++) {
            dates.add(now.plusDays(i));
        }

        List<Availability> allAvail = availabilityRepository.findAllByProjectAndDateIn(project, dates);
        Map<LocalDate, List<Availability>> availByDate = allAvail.stream()
                .collect(Collectors.groupingBy(Availability::getDate));

        for (LocalDate date : dates) {
            List<Availability> dayAvail = availByDate.getOrDefault(date, Collections.emptyList());
            if (isFreeForAll(dayAvail)) {
                result.add(date.format(formatter));
            }
            if (result.size() >= 10) break;
        }

        return result;
    }

    private List<BusyUserDto> busyUsers(List<Availability> availabilities) {
        return availabilities.stream()
                .filter(a -> a.getStatus() == AvailabilityStatus.BUSY)
                .map(a -> new BusyUserDto(
                        a.getUser().getId(),
                        a.getUser().getName(),
                        a.getUser().getColor()
                ))
                .toList();
    }
}
