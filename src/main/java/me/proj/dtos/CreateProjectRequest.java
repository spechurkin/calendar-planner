package me.proj.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProjectRequest {
  @NotBlank
  @Size(max = 120)
  private String name;
}
