package me.proj.dtos;

import java.time.LocalDate;
import java.util.List;

public record CalendarDay(LocalDate date, boolean today, boolean currentMonth, boolean freeForAll,
                          List<BusyUserDto> busyUsers) {
}