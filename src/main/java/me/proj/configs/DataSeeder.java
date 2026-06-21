package me.proj.configs;

import jakarta.transaction.Transactional;
import me.proj.entities.Project;
import me.proj.entities.User;
import me.proj.repos.AvailabilityRepository;
import me.proj.repos.ProjectRepository;
import me.proj.repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSeeder implements CommandLineRunner {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final MessageSource messageSource;

    public DataSeeder(ProjectRepository projectRepository, UserRepository userRepository, JdbcTemplate jdbcTemplate, MessageSource messageSource) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.messageSource = messageSource;
    }

    @Override
    public void run(String... args) throws Exception {
        String basicEvent = messageSource.getMessage("basicEvent", null, LocaleContextHolder.getLocale());

        Project defaultProject = projectRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> projectRepository.save(new Project(basicEvent)));

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
