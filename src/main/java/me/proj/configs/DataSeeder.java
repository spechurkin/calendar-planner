package me.proj.configs;

import me.proj.entities.Availability;
import me.proj.entities.Project;
import me.proj.entities.User;
import me.proj.repos.AvailabilityRepository;
import me.proj.repos.ProjectRepository;
import me.proj.repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seed(
      ProjectRepository projectRepository,
      UserRepository userRepository,
      AvailabilityRepository availabilityRepository,
      JdbcTemplate jdbcTemplate
  ) {

    return args -> {

      Project defaultProject = projectRepository.findAll()
          .stream()
          .findFirst()
          .orElseGet(() -> projectRepository.save(
              new Project("Общее мероприятие")
          ));

      if (userRepository.count() == 0) {
        User user = userRepository.save(
            new User(
                "Свят",
                "#3b82f6"
            )
        );
        defaultProject.getUsers().add(user);
        projectRepository.save(defaultProject);
      }

      migrateProjectMemberships(
          defaultProject,
          projectRepository,
          userRepository,
          jdbcTemplate
      );

      for (Availability availability : availabilityRepository.findAll()) {
        if (availability.getProject() == null) {
          availability.setProject(defaultProject);
          availabilityRepository.save(availability);
        }
      }
    };
  }

  private void migrateProjectMemberships(
      Project defaultProject,
      ProjectRepository projectRepository,
      UserRepository userRepository,
      JdbcTemplate jdbcTemplate
  ) {
    Integer membershipCount = jdbcTemplate.queryForObject(
        "select count(*) from project_user",
        Integer.class
    );

    if (membershipCount != null && membershipCount > 0) {
      return;
    }

    if (hasColumn(jdbcTemplate, "app_user", "project_id")) {
      List<Map<String, Object>> legacyUsers = jdbcTemplate.queryForList(
          "select id, project_id from app_user where project_id is not null"
      );

      for (Map<String, Object> legacyUser : legacyUsers) {
        Long userId = ((Number) legacyUser.get("id")).longValue();
        Long projectId = ((Number) legacyUser.get("project_id")).longValue();

        projectRepository.findById(projectId)
            .ifPresent(project -> userRepository.findById(userId)
                .ifPresent(user -> {
                  project.getUsers().add(user);
                  projectRepository.save(project);
                }));
      }
    }

    if (jdbcTemplate.queryForObject("select count(*) from project_user", Integer.class) == 0) {
      defaultProject.getUsers().addAll(userRepository.findAll());
      projectRepository.save(defaultProject);
    }
  }

  private boolean hasColumn(
      JdbcTemplate jdbcTemplate,
      String tableName,
      String columnName
  ) {
    return jdbcTemplate.queryForList("pragma_table_info('" + tableName + "')")
        .stream()
        .map(column -> column.get("name"))
        .anyMatch(columnName::equals);
  }
}
