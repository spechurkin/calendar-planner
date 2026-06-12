package me.proj.configs;

import jakarta.transaction.Transactional;
import me.proj.entities.Project;
import me.proj.entities.User;
import me.proj.repos.ProjectRepository;
import me.proj.repos.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSeeder {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataSeeder(ProjectRepository projectRepository, UserRepository userRepository, JdbcTemplate jdbcTemplate) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void seed() {
        Project defaultProject = projectRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> projectRepository.save(new Project("Общее мероприятие")));

        if (userRepository.count() == 0) {
            User user = userRepository.save(new User("Свят", "#3b82f6"));
            defaultProject = projectRepository.findById(defaultProject.getId()).orElseThrow();
            defaultProject.getUsers().add(user);
        }

        migrateProjectMemberships(defaultProject);
    }

    private void migrateProjectMemberships(Project defaultProject) {
        Integer membershipCount = jdbcTemplate.queryForObject(
                "select count(*) from project_user",
                Integer.class
        );

        if (membershipCount != null && membershipCount > 0) return;

        defaultProject = projectRepository.findById(defaultProject.getId()).orElseThrow();
        defaultProject.getUsers().addAll(userRepository.findAll());
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
