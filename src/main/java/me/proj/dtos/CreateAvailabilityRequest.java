package me.proj.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import me.proj.entities.AvailabilityStatus;

import java.time.LocalDate;

@Data
public class CreateAvailabilityRequest {

  @NotNull
  private Long projectId;

  @NotNull
  private Long userId;

  @NotNull
  private LocalDate date;

  @NotNull
  private AvailabilityStatus status;
}
