package me.proj.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
  @NotBlank
  @Size(max = 100)
  private String name;

  @NotBlank
  private String color;
}