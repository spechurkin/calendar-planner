package me.proj.configs;

import me.proj.entities.User;
import me.proj.repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seed(
      UserRepository repository
  ) {

    return args -> {

      if (repository.count() > 0) {
        return;
      }

      repository.save(
          new User(
              "Свят",
              "#3b82f6"
          )
      );

      repository.save(
          new User(
              "Мария",
              "#ef4444"
          )
      );

      repository.save(
          new User(
              "Антон",
              "#22c55e"
          )
      );
    };
  }
}