package me.proj.dtos;

import lombok.Getter;

import java.time.LocalDate;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CalendarDay {
  private final LocalDate date;
  private final boolean today;
  private final boolean currentMonth;
  private final boolean freeForAll;
  private final List<BusyUserDto> busyUsers;

  public CalendarDay(
      LocalDate date,
      boolean today,
      boolean currentMonth,
      boolean freeForAll,
      List<BusyUserDto> busyUsers
  ) {
    this.date = date;
    this.today = today;
    this.currentMonth = currentMonth;
    this.freeForAll = freeForAll;
    this.busyUsers = busyUsers;
  }
}