package me.proj.configs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handle(
      RuntimeException exception
  ) {

    return Map.of(
        "message",
        exception.getMessage()
    );
  }
}